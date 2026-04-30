package com.bank.platform.transaction_service.service;

import com.bank.platform.transaction_service.dto.*;
import com.bank.platform.transaction_service.entity.OutboxEvent;
import com.bank.platform.transaction_service.entity.Transaction;
import com.bank.platform.transaction_service.repository.ITransactionRepository;
import com.bank.platform.transaction_service.repository.IOutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.bank.platform.transaction_service.util.JsonUtil.toJson;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements ITransactionService {

    private final ITransactionRepository transactionRepository;
    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {

        Transaction transaction = new Transaction();
        transaction.setAccountId(transactionRequest.getAccountId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        // 🔥 EVENT ID (AGREGADO)
        String eventId = UUID.randomUUID().toString();

        // Crear evento
        TransactionEvent event = new TransactionEvent();
        event.setEventId(eventId);
        event.setAccountId(transactionRequest.getAccountId());
        event.setAmount(transactionRequest.getAmount());
        event.setType(transactionRequest.getType());

        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.builder()
                    .eventId(eventId) // 🔥 IMPORTANTE
                    .eventType("TRANSACTION_CREATED") // 🔥 CONSISTENTE
                    .payload(payload)
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("Error serializing event", e);
        }

        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getByAccount(String accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream().map(this::mapToResponse)
                .toList();
    }

    @Override
    public TransactionResponse mapToResponse(Transaction t) {
        TransactionResponse r = new TransactionResponse();
        r.setId(t.getId());
        r.setAccountId(t.getAccountId());
        r.setType(t.getType());
        r.setAmount(t.getAmount());
        r.setCreatedAt(t.getCreatedAt());
        return r;
    }

    @Override
    public void transfer(TransferRequest request) {

        if (request.getFromAccountId() == null || request.getToAccountId() == null) {
            throw new RuntimeException("accounts required");
        }

        if (request.getAmount() <= 0) {
            throw new RuntimeException("amount must be greater than 0");
        }

        String eventId = UUID.randomUUID().toString();

        // DEBIT
        Transaction debit = new Transaction();
        debit.setAccountId(request.getFromAccountId());
        debit.setAmount(request.getAmount());
        debit.setType("DEBIT");
        debit.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(debit);

        // CREDIT
        Transaction credit = new Transaction();
        credit.setAccountId(request.getToAccountId());
        credit.setAmount(request.getAmount());
        credit.setType("CREDIT");
        credit.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(credit);

        // Evento SAGA
        TransferEvent event = TransferEvent.builder()
                .eventId(eventId)
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .step("DEBIT")
                .build();

        try {
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.builder()
                    .eventId(eventId)
                    .eventType("TRANSFER") // 🔥 CONSISTENTE
                    .payload(payload) // ✅ SOLO ESTE
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outbox);

        } catch (Exception e) {
            throw new RuntimeException("error serializing event", e);
        }
    }
}

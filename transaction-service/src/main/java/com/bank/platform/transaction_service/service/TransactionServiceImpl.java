package com.bank.platform.transaction_service.service;

import com.bank.platform.transaction_service.dto.TransactionEvent;
import com.bank.platform.transaction_service.dto.TransactionRequest;
import com.bank.platform.transaction_service.dto.TransactionResponse;
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

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements ITransactionService{

    private final ITransactionRepository transactionRepository;
    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {

        Transaction  transaction = new Transaction();
        transaction.setAccountId(transactionRequest.getAccountId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        //Crear evento
        TransactionEvent event = new TransactionEvent();
        event.setEventId(UUID.randomUUID().toString());
        event.setAccountId(transactionRequest.getAccountId());
        event.setAmount(transactionRequest.getAmount());
        event.setType(transactionRequest.getType());

        try{
            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outbox = OutboxEvent.builder()
                    .eventType("TransactionCreated")
                    .payload(payload)
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxRepository.save(outbox);

        } catch (Exception e){
            throw new RuntimeException("Error serializing event");
        }

        return mapToResponse(transaction);
    }

    @Override
    public List<TransactionResponse> getByAccount(Long accountId) {
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
}

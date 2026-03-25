package com.bank.platform.transaction_service.service;

import com.bank.platform.transaction_service.client.IAccountClient;
import com.bank.platform.transaction_service.dto.AccountResponse;
import com.bank.platform.transaction_service.dto.TransactionEvent;
import com.bank.platform.transaction_service.dto.TransactionRequest;
import com.bank.platform.transaction_service.dto.TransactionResponse;
import com.bank.platform.transaction_service.entity.Transaction;
import com.bank.platform.transaction_service.kafka.TransactionProducer;
import com.bank.platform.transaction_service.repository.ITransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TransactionServiceImpl implements ITransactionService{

    private final ITransactionRepository transactionRepository;
    private final IAccountClient accountClient;
    private final TransactionProducer producer;

    @Override
    public TransactionResponse createTransaction(TransactionRequest transactionRequest) {

        AccountResponse account = accountClient.getAccountById(transactionRequest.getAccountId());

        if(account == null){
            throw new RuntimeException("account not found");
        }

        if("WITHDRAW".equalsIgnoreCase(transactionRequest.getType())){
            if(account.getBalance() < transactionRequest.getAmount()){
                throw new RuntimeException("amount not enough");
            }
            accountClient.updateBalance(transactionRequest.getAccountId(), -transactionRequest.getAmount());
        }

        if("DEPOSIT".equalsIgnoreCase(transactionRequest.getType())){
            accountClient.updateBalance(transactionRequest.getAccountId(), transactionRequest.getAmount());
        }

        Transaction  transaction = new Transaction();
        transaction.setAccountId(transactionRequest.getAccountId());
        transaction.setAmount(transactionRequest.getAmount());
        transaction.setType(transactionRequest.getType());
        transaction.setCreatedAt(LocalDateTime.now());
        transactionRepository.save(transaction);

        //Publicar evento
        TransactionEvent event = new TransactionEvent();
        event.setAccountId(transactionRequest.getAccountId());
        event.setAmount(transactionRequest.getAmount());
        event.setType(transactionRequest.getType());

        producer.sendTransaction(event);

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

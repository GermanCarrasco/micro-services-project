package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.bank.platform.account_service.entity.Account;
import com.bank.platform.account_service.repository.IAccountRepository;
import com.bank.platform.account_service.service.AccountServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

    private final IAccountRepository accountRepository;

    public TransactionConsumer(IAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @KafkaListener(topics = "transaction-topic",
    groupId = "account-group")
    public void consume(TransactionEvent event) {

        Account account = accountRepository.findById(event.getAccountId())
                .orElseThrow(()-> new RuntimeException("Account not found"));

        if("WITHDRAW".equalsIgnoreCase(event.getType())){
            account.setBalance(account.getBalance()-event.getAmount());
        }

        if("DEPOSIT".equalsIgnoreCase(event.getType())){
            account.setBalance(account.getBalance()+event.getAmount());
        }

        accountRepository.save(account);
    }
}

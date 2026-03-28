package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.bank.platform.account_service.entity.Account;
import com.bank.platform.account_service.entity.ProcessedEvent;
import com.bank.platform.account_service.repository.IAccountRepository;
import com.bank.platform.account_service.repository.IProcessedEventRepository;
import com.bank.platform.account_service.service.AccountServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionConsumer {

    private final IAccountRepository accountRepository;
    private final IProcessedEventRepository processedEventRepository;

    public TransactionConsumer(IAccountRepository accountRepository, IProcessedEventRepository processedEventRepository) {
        this.accountRepository = accountRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = "transaction-topic",
    groupId = "account-group")
    public void consume(String payload) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            TransactionEvent event = mapper.readValue(payload, TransactionEvent.class);

            //1. Validar si ya se proceso
            if(processedEventRepository.existsByEventId(event.getEventId())){
                System.out.println("Evento duplicado ignorado: "+event.getEventId());
                return;
            }

            Account account = accountRepository.findById(event.getAccountId())
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            //2. Validar balance
            if("WITHDRAW".equalsIgnoreCase(event.getType())){
                if(account.getBalance() < event.getAmount()){
                    throw new RuntimeException("Insuficiente funds");
                }
                account.setBalance(account.getBalance() - event.getAmount());
            }


            if ("DEPOSIT".equalsIgnoreCase(event.getType())) {
                account.setBalance(account.getBalance() + event.getAmount());
            }

            accountRepository.save(account);

            //3. Registrar como procesado
            ProcessedEvent processedEvent = ProcessedEvent.builder()
                    .eventId(event.getEventId())
                    .processedAt(LocalDateTime.now())
                    .build();
            try{
                processedEventRepository.save(processedEvent);
            } catch (DataIntegrityViolationException e){
                //Ya existe
                return;
            }


        } catch (Exception e) {
            throw new RuntimeException("Error processing event");
        }
    }

    @KafkaListener(topics = "transactions-dlq", groupId = "dlq-group")
    public void consumeDLQ(String payload) {

        System.out.println("Received DLQ: "+payload);
    }
}

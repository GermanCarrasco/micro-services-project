package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.bank.platform.account_service.entity.Account;
import com.bank.platform.account_service.entity.ProcessedEvent;
import com.bank.platform.account_service.repository.IAccountRepository;
import com.bank.platform.account_service.repository.IProcessedEventRepository;
import com.bank.platform.account_service.service.AccountServiceImpl;
import com.bank.platform.account_service.service.OutboxServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionConsumer {

    private final IAccountRepository accountRepository;
    private final IProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AccountProducer accountProducer;
    private final OutboxServiceImpl  outboxService;


    public TransactionConsumer(IAccountRepository accountRepository, IProcessedEventRepository processedEventRepository, AccountProducer accountProducer, OutboxServiceImpl outboxService) {
        this.accountRepository = accountRepository;
        this.processedEventRepository = processedEventRepository;
        this.accountProducer = accountProducer;
        this.outboxService = outboxService;
    }

    @KafkaListener(topics = "transaction-topic",
    groupId = "account-group")
    public void consume(String payload) {

        try {
            TransactionEvent event = mapper.readValue(payload, TransactionEvent.class);

            //1. Validar si ya se proceso
            if(processedEventRepository.existsByEventId(event.getEventId())){
                System.out.println("Evento duplicado ignorado: "+event.getEventId());
                return;
            }


            // PASO 1: DEBIT
            if ("DEBIT".equalsIgnoreCase(event.getStep())) {

                Account account = accountRepository.findById(event.getFromAccountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                if (account.getBalance() < event.getAmount()) {
                    throw new RuntimeException("Insufficient funds");
                }

                account.setBalance(account.getBalance() - event.getAmount());
                accountRepository.save(account);

                System.out.println("DEBIT realizado");

                //Crear event CREDIT
                TransactionEvent creditEvent = TransactionEvent.builder()
                        .eventId(event.getEventId()) //mismo ID , esto es clave en SAGA
                        .step("CREDIT")
                        .amount(event.getAmount())
                        .fromAccountId(event.getFromAccountId())
                        .toAccountId(event.getToAccountId())
                        .build();

                outboxService.saveEvent(
                        creditEvent,
                        "CREDIT",
                        creditEvent.getEventId()
                );

            // PASO 2: CREDIT
            } else if ("CREDIT".equalsIgnoreCase(event.getStep())) {


                try {

                    Account account = accountRepository.findById(event.getToAccountId())
                            .orElseThrow(() -> new RuntimeException("Account not found"));

                    account.setBalance(account.getBalance() + event.getAmount());
                    accountRepository.save(account);

                    System.out.println("CREDIT realizado");

                } catch (Exception e) {

                    System.out.println("Error on CREDIT -> Rollback Bigining");

                    //Crear evento de rollback
                    TransactionEvent rollbackEnvent = TransactionEvent.builder()
                            .eventId(event.getEventId()+"-ROLLBACK")
                            .step("ROLLBACK")
                            .amount(event.getAmount())
                            .fromAccountId(event.getFromAccountId())
                            .toAccountId(event.getToAccountId())
                            .status("FAILED")
                            .reason(e.getMessage())
                            .build();

                    outboxService.saveEvent(
                            rollbackEnvent,
                            "ROLLBACK",
                            rollbackEnvent.getEventId()
                    );
                }

            } else if ("ROLLBACK".equalsIgnoreCase(event.getStep())) {

                Account account = accountRepository.findById(event.getFromAccountId())
                        .orElseThrow(() -> new RuntimeException("Account not found"));

                account.setBalance(account.getBalance() + event.getAmount());
                accountRepository.save(account);

                System.out.println("ROLLBACK realizado (dinero retornado)");

            }


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

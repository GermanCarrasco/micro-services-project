package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.bank.platform.account_service.entity.Account;
import com.bank.platform.account_service.entity.ProcessedEvent;
import com.bank.platform.account_service.repository.IAccountRepository;
import com.bank.platform.account_service.repository.IProcessedEventRepository;
import com.bank.platform.account_service.service.AccountServiceImpl;
import com.bank.platform.account_service.service.OutboxServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import java.time.LocalDateTime;
//import io.opentelemetry.api.trace.Span;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.Span;

@Component
public class TransactionConsumer {

    private static final Logger log = LoggerFactory.getLogger(TransactionConsumer.class);

    private final IAccountRepository accountRepository;
    private final IProcessedEventRepository processedEventRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final AccountProducer accountProducer;
    private final OutboxServiceImpl  outboxService;
    private final MeterRegistry meterRegistry;
    private final Tracer tracer;

    public TransactionConsumer(IAccountRepository accountRepository, IProcessedEventRepository processedEventRepository,
                               AccountProducer accountProducer, OutboxServiceImpl outboxService, MeterRegistry meterRegistry,
                               Tracer tracer) {
        this.accountRepository = accountRepository;
        this.processedEventRepository = processedEventRepository;
        this.accountProducer = accountProducer;
        this.outboxService = outboxService;
        this.meterRegistry = meterRegistry;
        this.tracer = tracer;
    }

    @KafkaListener(topics = "transactions-topic",
    groupId = "account-group")
    public void consume(String payload) {


        TransactionEvent event = null;
        try {

            event = mapper.readValue(payload, TransactionEvent.class);

//            Span currentSpan = tracer.currentSpan();
////            currentSpan.tag("correlationId", event.getCorrelationId());
////            currentSpan.tag("eventId", event.getEventId());
//            currentSpan.tag("step", event.getStep());

//            MDC.put("correlationId", event.getCorrelationId()); //Con esto puedo acceder en la configuracion del .yml

            System.out.println(
//                    "CID: " + event.getCorrelationId() +
                            " | STEP: " + event.getStep() +
                            " | EVENT: " + event.getEventId()
            );

            //1. Validar si ya se proceso
//            if (processedEventRepository.existsByEventId(event.getEventId())) {
//                System.out.println("Evento duplicado ignorado: " + event.getEventId());
//                return;
//            }


            // PASO 1: DEBIT
            if ("DEBIT".equalsIgnoreCase(event.getStep())) {

                Account account = accountRepository.findByAccountNumber(event.getFromAccountId());
//                        .orElseThrow(() -> new RuntimeException("Account not found"));

                if (account.getBalance() < event.getAmount()) {
                    throw new RuntimeException("Insufficient funds");
                }

                account.setBalance(account.getBalance() - event.getAmount());
                accountRepository.save(account);

                System.out.println("DEBIT realizado");
                log.info(
                        "CID={} EVENT={} STEP={} MESSAGE={}",
//                        event.getCorrelationId(),
                        event.getEventId(),
                        event.getStep()
                        , "DEBIT realizado");

                //Crear event CREDIT
                TransactionEvent creditEvent = TransactionEvent.builder()
                        .eventId(event.getEventId()) //mismo ID , esto es clave en SAGA
//                        .correlationId(event.getCorrelationId())
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

                    Account account = accountRepository.findByAccountNumber(event.getToAccountId());
//                            .orElseThrow(() -> new RuntimeException("Account not found"));

                    account.setBalance(account.getBalance() + event.getAmount());
                    accountRepository.save(account);

                    log.info(
                            "CID={} EVENT={} STEP={} MESSAGE={}",
//                            event.getCorrelationId(),
                            event.getEventId(),
                            event.getStep(),
                            "CREDIT realizado"
                    );


                } catch (Exception e) {

                    System.out.println("Error on CREDIT -> Rollback Bigining");

                    //Crear evento de rollback
                    TransactionEvent rollbackEnvent = TransactionEvent.builder()
                            .eventId(event.getEventId() + "-ROLLBACK")
//                            .correlationId(event.getCorrelationId())
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

                Account account = accountRepository.findByAccountNumber(event.getFromAccountId());
//                        .orElseThrow(() -> new RuntimeException("Account not found"));

                account.setBalance(account.getBalance() + event.getAmount());
                accountRepository.save(account);

                log.info(
                        "CID={} EVENT={} STEP={} MESSAGE={}",
//                        event.getCorrelationId(),
                        event.getEventId(),
                        event.getStep(),
                        "ROLLBACK realizado (dinero retornado)"
                );

            }


            //3. Registrar como procesado
            ProcessedEvent processedEvent = ProcessedEvent.builder()
                    .eventId(event.getEventId())
                    .processedAt(LocalDateTime.now())
                    .build();

            meterRegistry.counter("transactions.processed", "step", event.getStep()).increment();

            try {
                processedEventRepository.save(processedEvent);
            } catch (DataIntegrityViolationException e) {
                //Ya existe
                return;
            }


        } catch (Exception e) {
            meterRegistry.counter("transactions.failed",
                    "step", event.getStep()
            ).increment();
            throw new RuntimeException("Error processing event");
        }

        MDC.clear();
    }

    @KafkaListener(topics = "transactions-dlq", groupId = "dlq-group")
    public void consumeDLQ(String payload) throws JsonProcessingException {

        TransactionEvent event = mapper.readValue(payload, TransactionEvent.class);

        meterRegistry.counter("transactions.dlq").increment();

        log.info(
                "CID={} EVENT={} STEP={} MESSAGE={}",
//                event.getCorrelationId(),
                event.getEventId(),
                event.getStep(),
                "Received DLQ: "+payload
        );

    }
}

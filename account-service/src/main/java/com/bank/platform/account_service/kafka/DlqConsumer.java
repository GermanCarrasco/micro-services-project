package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.bank.platform.account_service.entity.FailedEvent;
import com.bank.platform.account_service.repository.IFailedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DlqConsumer {

    private final IFailedEventRepository iFailedEventRepository;

    public DlqConsumer(IFailedEventRepository iFailedEventRepository) {
        this.iFailedEventRepository = iFailedEventRepository;
    }

    @KafkaListener(topics = "transactions-dlq", groupId = "dlq-group")
    public void consumeDLQ(String payload) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        TransactionEvent event = mapper.readValue(payload, TransactionEvent.class);

        System.out.println("Evento en DLQ: " + payload);

        FailedEvent failedEvent = FailedEvent.builder()
                .eventId(event.getEventId())
                .payload(payload)
                .errorMessage("Error en procesamiento (ver logs)")
                .createdAt(LocalDateTime.now())
                .build();

        iFailedEventRepository.save(failedEvent);
    }
}

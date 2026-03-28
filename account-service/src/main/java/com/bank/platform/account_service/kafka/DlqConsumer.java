package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.entity.FailedEvent;
import com.bank.platform.account_service.repository.IFailedEventRepository;
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
    public void consumeDLQ(String payload) {

        System.out.println("Evento en DLQ: " + payload);

        FailedEvent failedEvent = FailedEvent.builder()
                .payload(payload)
                .errorMessage("Error Procesando evnto")
                .createdAt(LocalDateTime.now())
                .build();

        iFailedEventRepository.save(failedEvent);
    }
}

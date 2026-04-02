package com.bank.platform.account_service.service;

import com.bank.platform.account_service.entity.OutboxEvent;
import com.bank.platform.account_service.repository.IOutboxEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OutboxServiceImpl implements IOutboxService{

    private final IOutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void saveEvent(Object event, String eventType, String eventId) {

        try {

            String payload = objectMapper.writeValueAsString(event);

            OutboxEvent outboxEvent = OutboxEvent.builder()
                    .eventType(eventType)
                    .eventId(eventId)
                    .payload(payload)
                    .status("PENDING")
                    .createdAt(LocalDateTime.now())
                    .build();

            outboxEventRepository.save(outboxEvent);

        } catch (Exception e){
            throw new RuntimeException("Error while saving outbox event" +  e.getMessage());
        }
    }
}

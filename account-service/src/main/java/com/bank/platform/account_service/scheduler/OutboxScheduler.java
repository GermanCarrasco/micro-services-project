package com.bank.platform.account_service.scheduler;

import com.bank.platform.account_service.entity.OutboxEvent;
import com.bank.platform.account_service.repository.IOutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;



@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final IOutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedRate = 5000)
    public void processOutbox(){

        List<OutboxEvent> events = outboxEventRepository.findByStatus("PENDING");

        for(OutboxEvent event: events){
            try {

                kafkaTemplate.send("transaction-topic",event.getPayload());

                event.setStatus("SENT");
                outboxEventRepository.save(event);

            }catch(Exception e){
                System.out.println("Error sending Outbox event: "+e);
            }
        }

    }
}

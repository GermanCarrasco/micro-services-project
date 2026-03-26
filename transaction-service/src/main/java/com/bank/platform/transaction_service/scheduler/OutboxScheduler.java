package com.bank.platform.transaction_service.scheduler;

import com.bank.platform.transaction_service.dto.TransactionResponse;
import com.bank.platform.transaction_service.entity.OutboxEvent;
import com.bank.platform.transaction_service.repository.IOutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@EnableScheduling
public class OutboxScheduler {
    private final IOutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxScheduler(IOutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    //cada 5 seg
    @Scheduled(fixedRate = 5000)
    public void processOutbox(){

        List<OutboxEvent> events = outboxRepository.findTop10ByStatus("PENDING");

        //VALIDACIÓN
        if (events == null || events.isEmpty()) {
            System.out.println("No hay eventos pendientes en outbox");
            return;
        }

        for (OutboxEvent event : events) {

            //Intento de bloqueo
            int updated = outboxRepository.markAsProcessing(event.getId());

            if(updated == 0){
                //otro proceso lo tomo
                continue;
            }

            try {
                kafkaTemplate.send("transactions-topic", event.getPayload());

                event.setStatus("SENT");
                outboxRepository.save(event);

            } catch (Exception e) {

                //Vuelve a pending para retry
                event.setStatus("PENDING");
                outboxRepository.save(event);

                System.out.println("Error enviando evento ID " + event.getId() + ": " + e.getMessage());
            }
        }
    }
}

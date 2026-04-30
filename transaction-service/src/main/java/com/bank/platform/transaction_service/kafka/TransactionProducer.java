package com.bank.platform.transaction_service.kafka;

import com.bank.platform.transaction_service.dto.TransactionEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    public TransactionProducer(KafkaTemplate<String, TransactionEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransaction(TransactionEvent event) {
        kafkaTemplate.send("transactions-topic", event);
    }
}

package com.bank.platform.account_service.kafka;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AccountProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(TransactionEvent transactionEvent) {

        try{
            String message = objectMapper.writeValueAsString(transactionEvent);
            kafkaTemplate.send("transaction-topic",message);

            System.out.println("Evento enviado: " + transactionEvent.getStep());

        }catch(Exception e){
            throw new RuntimeException("Error enviando evento");
        }

    }
}

package com.bank.platform.account_service.service;

import com.bank.platform.account_service.dto.TransactionEvent;
import com.bank.platform.account_service.entity.FailedEvent;
import com.bank.platform.account_service.repository.IFailedEventRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetryServiceImpl implements IRetryService{

    private final IFailedEventRepository failedEventRepository;
    private final KafkaTemplate<String,String>  kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void retryFailedEvents() {
        List<FailedEvent> failedEvents = failedEventRepository.findByStatus("PENDING");

        for(FailedEvent event: failedEvents){

            try {

                TransactionEvent events = mapper.readValue(
                        event.getPayload(),
                        TransactionEvent.class
                );

                System.out.print(
                        "RETRY | CID: " + events.getCorrelationId() +
                                " | EVENT: " + events.getEventId() +
                                " | STEP: " + events.getStep()
                );

                kafkaTemplate.send("transaction-topic", event.getPayload());

                event.setStatus("SUCCESS");
                failedEventRepository.save(event);

                System.out.println("Retry Success");

            } catch (Exception e) {

                event.setRetryCount(event.getRetryCount()+1);

                if(event.getRetryCount()>=3){
                    event.setStatus("FAILED");
                }

                failedEventRepository.save(event);

                System.out.println("Retry Failed");
            }
        }
    }
}

package com.bank.platform.account_service.scheduler;

import com.bank.platform.account_service.service.RetryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RetryScheduler {

    private final RetryServiceImpl  retryService;

    @Scheduled(fixedRate = 10000)
    public void retry(){
        retryService.retryFailedEvents();
    }
}

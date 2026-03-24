package com.bank.platform.transaction_service.client;

import com.bank.platform.transaction_service.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "account-service",url = "http://localhost:8082")
public interface IAccountClient {

    @GetMapping("/accounts/{id}")
    AccountResponse getAccountById(@PathVariable Long id);

    @PutMapping("/accounts/{id}/balance")
    void updateBalance(@PathVariable Long id, @RequestParam Double amount);
}

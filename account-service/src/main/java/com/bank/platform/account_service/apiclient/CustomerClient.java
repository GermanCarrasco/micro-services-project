package com.bank.platform.account_service.apiclient;

import com.bank.platform.account_service.dto.CustomerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "CUSTOMER-SERVICE")
public interface CustomerClient {

    @GetMapping("/customer/{id}")
    CustomerResponse getCustomerById(@PathVariable("id") Long id);
}

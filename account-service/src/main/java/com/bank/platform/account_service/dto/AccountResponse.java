package com.bank.platform.account_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountResponse {
    private Long id;
    private Double balance;
    private String accountNumber;
    private CustomerResponse customer;
}

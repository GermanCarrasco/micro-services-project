package com.bank.platform.account_service.dto;

import lombok.Data;

@Data
public class AccountRequest {
    private Long customerId;
    private Double initialBalance;
}

package com.bank.platform.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransactionRequest {
    private String accountId;
    private String type;
    private Double amount;
}

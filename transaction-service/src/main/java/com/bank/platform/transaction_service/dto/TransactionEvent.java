package com.bank.platform.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {
    private String eventId;
    private String accountId;
    private String type;
    private Double amount;
}

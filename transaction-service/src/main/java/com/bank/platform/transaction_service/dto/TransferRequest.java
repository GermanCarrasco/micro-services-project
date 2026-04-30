package com.bank.platform.transaction_service.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransferRequest {
    private String fromAccountId;
    private String toAccountId;
    private Double amount;
}

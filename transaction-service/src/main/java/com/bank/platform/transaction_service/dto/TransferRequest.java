package com.bank.platform.transaction_service.dto;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransferRequest {
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
}

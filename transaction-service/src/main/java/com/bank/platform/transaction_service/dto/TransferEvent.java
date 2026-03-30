package com.bank.platform.transaction_service.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TransferEvent {
    private String eventId;
    private String step; //TRANSFER_REQUESTED , DEBIT_SUCCESS, etc
    private Long fromAccountId;
    private Long toAccountId;
    private Double amount;
}

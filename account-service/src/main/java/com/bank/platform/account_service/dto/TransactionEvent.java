package com.bank.platform.account_service.dto;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionEvent {
    private String eventId;
    private Long accountId;
    private String type;
    private Double amount;
    private String step; // DEBIT | CREDIT
    private Long fromAccountId;
    private Long toAccountId;
    private String status; //SUCCESS | FAILDE
    private String reason; //opcional (error)
    private String correlationId; //lo agregue el 3-4-2026 fue lo ultimo que hice, continuar ahi
}

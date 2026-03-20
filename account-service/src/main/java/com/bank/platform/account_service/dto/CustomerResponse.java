package com.bank.platform.account_service.dto;

import lombok.Data;

@Data
public class CustomerResponse {
    private Long id;
    private String fullName;
    private String email;
}

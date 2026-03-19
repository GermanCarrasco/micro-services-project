package com.bank.platform.auth_service.dto;

import lombok.Data;

@Data
public class RefreshRequest {
    private String refreshToken;
}

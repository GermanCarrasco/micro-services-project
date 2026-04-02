package com.bank.platform.account_service.entity;


import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String eventId;
    private String eventType;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status; //PENDING , SENT

    private LocalDateTime createdAt;
}

package com.bank.platform.account_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "failed_events",schema = "account")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class FailedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private String errorMessage;
    private LocalDateTime createdAt;
    private String eventId;
    private int retryCount;
    private String status;
    private LocalDateTime lastAttemptAt;

}

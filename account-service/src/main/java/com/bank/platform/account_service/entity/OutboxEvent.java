package com.bank.platform.account_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Getter
@Setter
@Table(name = "outbox_event",schema = "account")
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

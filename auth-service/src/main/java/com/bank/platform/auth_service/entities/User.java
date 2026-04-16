package com.bank.platform.auth_service.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users",schema = "auth")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String username;
    private String password;
    private String role;
}

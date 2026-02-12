package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    private String discriminator;

    private String iconUrl;

    private UserStatus status;

    private String statusMessage;

    private String provider;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.USER;

}

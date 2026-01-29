package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"server_id", "user_id"})
})
@NoArgsConstructor
@Getter
public class ServerMember {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

    private OffsetDateTime joinedAt;

    public ServerMember(Server server, User user, Role role, OffsetDateTime joinedAt) {
        this.server = server;
        this.user = user;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public boolean isAdmin() {
        return role == Role.OWNER || role == Role.ADMIN;
    }

}

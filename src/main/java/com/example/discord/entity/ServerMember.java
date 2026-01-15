package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"server_id", "user_id"})
})
public class ServerMember {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Enumerated(EnumType.STRING)
    private Role role;

    public ServerMember(Server server, User user, Role role) {
    }
}

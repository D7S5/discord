package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Channel {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(nullable = false)
    private String name;

    public Channel(Server server, String name, ChannelType type) {
    }
}

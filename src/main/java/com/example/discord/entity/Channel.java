package com.example.discord.entity;

import jakarta.persistence.*;

@Entity
public class Channel {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Server server;

    @Enumerated(EnumType.STRING)
    private ChannelType type;

    @Column(nullable = false)
    private String name;
}

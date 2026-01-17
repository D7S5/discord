package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Channel channel;

    @ManyToOne(fetch = FetchType.LAZY)
    private User sender;

    @Column(nullable = false)
    private String content;

    private OffsetDateTime createdAt;
}

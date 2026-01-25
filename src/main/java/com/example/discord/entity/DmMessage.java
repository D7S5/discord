package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "dm_messages",
    indexes = {
        @Index(columnList = "room_id, id")
    })
@Getter
@NoArgsConstructor
public class DmMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "room_id")
//    private DmRoom room;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "sender_id")
//    private User sender;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    public DmMessage(String roomId, String senderId, String content) {
        this.roomId = roomId;
        this.senderId = senderId;
        this.content = content;
    }
}

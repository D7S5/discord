package com.example.discord.dto;

import com.example.discord.entity.Message;
import com.example.discord.entity.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long id;
    private String senderId;
    private String senderName;
    private String senderIconUrl;
    private String content;
    private MessageType type;
    private OffsetDateTime createdAt;

    public static MessageResponse from(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderId(m.getSender().getId())
                .senderName(m.getSender().getUsername())
                .senderIconUrl(m.getSender().getIconUrl())
                .content(m.getContent())
                .type(m.getType())
                .createdAt(m.getCreatedAt())
                .build();
    }
}

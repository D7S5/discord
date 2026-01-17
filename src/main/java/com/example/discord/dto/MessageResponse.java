package com.example.discord.dto;

import com.example.discord.entity.Message;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@Builder
public class MessageResponse {
    private Long id;
    private String senderName;
    private String content;
    private OffsetDateTime createdAt;

    public static MessageResponse from(Message m) {
        return MessageResponse.builder()
                .id(m.getId())
                .senderName(m.getSender().getUsername())
                .content(m.getContent())
                .createdAt(m.getCreatedAt())
                .build();
    }
}

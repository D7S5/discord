package com.example.discord.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {
    private Long channelId;
    private Long senderId;
    private String senderName;
    private String content;
    private OffsetDateTime createAt;
}

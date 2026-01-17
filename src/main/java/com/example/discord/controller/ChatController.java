package com.example.discord.controller;

import com.example.discord.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@RestController
public class ChatController {

    @MessageMapping("/chat.send")
    @SendTo("/topic/channels/{channelId}")
    public ChatMessage send(
            @Payload ChatMessage message,
            @DestinationVariable Long channelId
    ) {
        message.setCreateAt(OffsetDateTime.now());
        return message;
    }
}

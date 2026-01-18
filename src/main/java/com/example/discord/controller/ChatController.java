package com.example.discord.controller;

import com.example.discord.dto.ChatMessage;
import com.example.discord.dto.MessageResponse;
import com.example.discord.entity.Message;
import com.example.discord.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
public class ChatController {

    private final MessageService messageService;
//    @MessageMapping("/chat.send")
//    @SendTo("/topic/channels/{channelId}")
//    public MessageResponse send(
//            @Payload ChatMessage message,
//            @DestinationVariable Long channelId
//    ) {
//        Message saved = messageService.save(
//                channelId,
//                message.getSenderId(),
//                message.getContent()
//        );
//        return MessageResponse.from(saved);
//    }
}

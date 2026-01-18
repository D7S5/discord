package com.example.discord.controller;

import com.example.discord.dto.ChatMessageRequest;
import com.example.discord.dto.MessageResponse;
import com.example.discord.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RequiredArgsConstructor
@RestController
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    @MessageMapping("/channels/{channelId}/messages")
    public void sendMessage(
            @DestinationVariable Long channelId,
            ChatMessageRequest request,
            SimpMessageHeaderAccessor accessor
            ) {
        String userId = (String) accessor.getSessionAttributes().get("userId");
        System.out.println("accessor = " + userId);


        if (userId == null) {
            throw new IllegalStateException("WebSocket 인증 정보 없음");
        }

        MessageResponse saved =
                messageService.save(channelId, userId, request);

        messagingTemplate.convertAndSend(
                "/topic/channels/" + channelId,
                saved
        );
    }
}

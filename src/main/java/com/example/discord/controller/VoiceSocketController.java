package com.example.discord.controller;

import com.example.discord.dto.voice.VoiceJoinRequest;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.VoiceRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VoiceSocketController {

    private final VoiceRoomService voiceRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/voice.join")
    public void join(@Payload VoiceJoinRequest req,
                     Principal principal) {
        voiceRoomService.join(req.getChannelId(), principal.getName());

//        System.out.println("getUsers = " + voiceRoomService.getUsers(req.getChannelId()));

        messagingTemplate.convertAndSend(
                "/topic/voice/" + req.getChannelId(),
                voiceRoomService.getUsers(req.getChannelId())
        );
    }

    @MessageMapping("/voice.leave")
    public void leave(@Payload VoiceJoinRequest req,
                      Principal principal) {

        voiceRoomService.leave(req.getChannelId(), principal.getName());

        messagingTemplate.convertAndSend(
                "/topic/voice/" + req.getChannelId(),
                voiceRoomService.getUsers(req.getChannelId())
        );
    }

    @MessageMapping("/voice/{roomId}/speaking")
    public void speaking(
            @DestinationVariable Long roomId,
            @Payload Map<String, Boolean> body,
            Principal principal
            ) {
        voiceRoomService.setSpeaking(
                roomId,
                principal.getName(),
                body.get("speaking")
        );

        messagingTemplate.convertAndSend(
                "/topic/voice/" + roomId,
                voiceRoomService.getUsers(roomId)
        );
    }
}

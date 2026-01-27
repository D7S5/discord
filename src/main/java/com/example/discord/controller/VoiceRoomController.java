package com.example.discord.controller;

import com.example.discord.dto.voice.VoiceEvent;
import com.example.discord.dto.voice.VoiceJoinRequest;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.VoiceRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/channels/{serverId}/channels")
@RequiredArgsConstructor
public class VoiceRoomController {

    private final VoiceRoomService voiceRoomService;
    private final SimpMessagingTemplate messagingTemplate;

    /** 보이스 채널 입장 */
    @PostMapping("/join")
    public void join(@RequestBody VoiceJoinRequest req,
                     @AuthenticationPrincipal UserPrincipal principal) {
        String userId = principal.getId();

        voiceRoomService.join(req.getServerId(), req.getChannelId(), userId);

        messagingTemplate.convertAndSend(
                "/topic/voice/" + req.getServerId(),
                new VoiceEvent("JOIN", req.getChannelId(), userId)
        );
    }

    /** 보이스 채널 퇴장 */
    @PostMapping("/leave")
    public void leave(@RequestBody VoiceJoinRequest req, Principal principal) {
        String userId = principal.getName();

        voiceRoomService.leave(req.getServerId(), req.getChannelId(), userId);

        messagingTemplate.convertAndSend(
                "/topic/voice/" + req.getServerId(),
                new VoiceEvent("LEAVE", req.getChannelId(), userId)
        );
    }
}

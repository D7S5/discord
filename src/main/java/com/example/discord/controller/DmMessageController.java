package com.example.discord.controller;

import com.example.discord.dto.DmMessageResponse;
import com.example.discord.dto.DmSendRequest;
import com.example.discord.entity.DmMessage;
import com.example.discord.service.DmMessageService;
import com.example.discord.service.DmRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/dm/messages")
@RequiredArgsConstructor
@Slf4j
public class DmMessageController {

    private final DmMessageService dmMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private final DmRoomService dmRoomService;

    @GetMapping("/{roomId}")
    public List<DmMessageResponse> getMessages(@PathVariable String roomId) {
        return dmMessageService.getMessages(roomId);
    }

    @MessageMapping("/dm.send")
    public void sendDm(@Payload DmSendRequest request,
                       Principal principal) {

        String senderId = principal.getName();

        // 1️⃣ DB 저장
        DmMessage message = dmMessageService.save(
                request.getRoomId(),
                senderId,
                request.getContent()
        );

        // 2️⃣ 상대방에게 전송
        String receiverId = dmRoomService.getOtherUserId(
                request.getRoomId(),
                senderId
        );

        messagingTemplate.convertAndSendToUser(
                receiverId,
                "/queue/dm",
                DmMessageResponse.from(message)

        );

        messagingTemplate.convertAndSendToUser(
                senderId,
                "/queue/dm",
                DmMessageResponse.from(message)
        );
    }
}

package com.example.discord.component;

import com.example.discord.dto.PresenceMessage;
import com.example.discord.redis.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() == null) {
            log.warn("accessor.getUser is null");
            return;
        }

        String userId = accessor.getUser().getName();
        Long serverId = (Long) accessor.getSessionAttributes().get("serverId");

        System.out.println("Disconnect serverId" + serverId);
        System.out.println("Disconnect userId" + userId);

        presenceService.offline(serverId, userId);

        messagingTemplate.convertAndSend(
                "/topic/presence",
                new PresenceMessage(userId, "OFFLINE")
        );
    }
}

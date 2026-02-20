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

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class
WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());

        String userId = accessor.getUser() != null
                ? accessor.getUser().getName()
                : null;
        if (userId == null) return;

        @SuppressWarnings("unchecked")
        Set<Long> serverIds =
                (Set<Long>) accessor.getSessionAttributes().get("servers");

        if (serverIds == null) return;

        for (Long serverId : serverIds) {
            presenceService.offline(serverId, userId);
//            log.info("🔴 OFFLINE user={} server={}", userId, serverId);
        }

        messagingTemplate.convertAndSend(
                "/topic/presence",
                new PresenceMessage(userId, "OFFLINE")
        );
    }
}

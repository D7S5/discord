package com.example.discord.security;

import com.example.discord.redis.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;
    private final PresenceService presenceService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String userId = accessor.getUser() != null
                    ? accessor.getUser().getName()
                    : null;
            if (destination == null || userId == null) {
                return message;
            }

            if (destination.startsWith("/topic/presence/")) {
                Long serverId = extractServerId(destination);

                @SuppressWarnings("unchecked")
                Set<Long> servers = (Set<Long>) accessor
                        .getSessionAttributes()
                        .computeIfAbsent("servers", k -> new HashSet<Long>());

                servers.add(serverId);

                presenceService.online(serverId, userId);

                log.info("🟢 ONLINE user={} server={}", userId, serverId);

            }
        }

        // STOMP CONNECT 프레임일 때만
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalStateException("WebSocket Authorization 헤더 없음");
            }

            String token = authHeader.substring(7);

            String userId = jwtProvider.getUserId(token);

            accessor.setUser(new StompPrincipal(userId));
            // ⭐ 세션에 저장
            accessor.getSessionAttributes().put("userId", userId);
        }
        return message;
    }

    private Long extractServerId(String destination) {
        // /topic/presence/{serverId}
        return Long.valueOf(destination.substring("/topic/presence/".length()));
    }
}

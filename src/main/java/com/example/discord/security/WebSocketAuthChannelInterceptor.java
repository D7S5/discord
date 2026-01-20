package com.example.discord.security;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // STOMP CONNECT 프레임일 때만
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authHeader = accessor.getFirstNativeHeader("Authorization");
            String serverIdHeader = accessor.getFirstNativeHeader("serverId");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new IllegalStateException("WebSocket Authorization 헤더 없음");
            }

            if (serverIdHeader == null) {
                throw new IllegalStateException("ServerId 저장 안됨");
            }

            String token = authHeader.substring(7);

            String userId = jwtProvider.getUserId(token);
            Long serverId = Long.valueOf(serverIdHeader);

            accessor.setUser(new StompPrincipal(userId));

            // ⭐ 세션에 저장
            accessor.getSessionAttributes().put("userId", userId);
            accessor.getSessionAttributes().put("serverId", serverId);
        }
        return message;
    }
}

package com.example.discord.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PresenceService {

    private final StringRedisTemplate redis;
    private static final long TTL = 60;
    private static String key = "online:server:";

    public void online(Long serverId, String userId) {
        redis.opsForSet().add(key + serverId, userId);
        redis.expire(key + serverId, TTL, TimeUnit.SECONDS);
    }

    public void offline(Long serverId, String userId) {
        redis.opsForSet().remove(key + serverId, userId);
    }

    public boolean isOnline(Long serverId, String userId) {
        return Boolean.TRUE.equals(
                redis.opsForSet().isMember(key + serverId, userId));
    }

    public Set<String> onlineUsers(Long serverId) {
        return redis.opsForSet().members(key + serverId);
    }
}

package com.example.discord.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VoiceRoomService {

    private final RedisTemplate<String, Object> redis;

    private String usersKey(Long roomId) {
        return "voice:room:" + roomId + ":users";
    }

    private String speakingKey(Long roomId) {
        return "voice:room:" + roomId + ":speaking";
    }

    /* 🔊 입장 */
    public void join(Long roomId, String userId) {
        redis.opsForSet().add(usersKey(roomId), userId);
        redis.opsForHash().put(speakingKey(roomId), userId, false);
    }

    /* 🚪 퇴장 */
    public void leave(Long roomId, String userId) {
        redis.opsForSet().remove(usersKey(roomId), userId);
        redis.opsForHash().delete(speakingKey(roomId), userId);
    }

    /* 🎙 말하는 중 */
    public void setSpeaking(Long roomId, String userId, boolean speaking) {
        redis.opsForHash().put(speakingKey(roomId), userId, speaking);
    }

    /* 👥 유저 목록 */
    public List<Map<String, Object>> getUsers(Long roomId) {
        Set<Object> users = redis.opsForSet().members(usersKey(roomId));
        if (users == null) return Collections.emptyList();

        Map<Object, Object> speakingMap =
                redis.opsForHash().entries(speakingKey(roomId));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object userId : users) {
            Map<String, Object> user = new HashMap<>();
            user.put("userId", userId);
            user.put(
                    "speaking",
                    Boolean.TRUE.equals(speakingMap.get(userId))
            );
            result.add(user);
        }

        return result;
    }
}

package com.example.discord.service;

import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VoiceRoomService {

    private final RedisTemplate<String, Object> redis;
    private final UserRepository userRepository;

    private String usersKey(Long roomId) {
        return "voice:room:" + roomId + ":users";
    }

    private String speakingKey(Long roomId) {
        return "voice:room:" + roomId + ":speaking";
    }

    public void join(Long roomId, String userId) {
        redis.opsForSet().add(usersKey(roomId), userId);
        redis.opsForHash().put(speakingKey(roomId), userId, false);
    }

    public void leave(Long roomId, String userId) {
        redis.opsForSet().remove(usersKey(roomId), userId);
        redis.opsForHash().delete(speakingKey(roomId), userId);
    }

    public void setSpeaking(Long roomId, String userId, boolean speaking) {
        redis.opsForHash().put(speakingKey(roomId), userId, speaking);
    }

    public List<Map<String, Object>> getUsers(Long roomId) {
        Set<Object> rawusers = redis.opsForSet().members(usersKey(roomId));
        if (rawusers == null || rawusers.isEmpty()) return Collections.emptyList();

        List<String> userIds = rawusers.stream()
                            .map(o -> String.valueOf(o.toString())
                            ).toList();

        Map<String, String> usernameMap =
                userRepository.findByIdIn(userIds).stream()
                        .collect(Collectors.toMap(
                                User::getId,
                                User::getUsername
                        ));

        Map<Object, Object> speakingMap =
                redis.opsForHash().entries(speakingKey(roomId));

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object userId : userIds) {
            Map<String, Object> user = new HashMap<>();
            user.put("userId", userId);
            user.put("username", usernameMap.get(userId));
            user.put(
                    "speaking",
                    Boolean.TRUE.equals(speakingMap.get(userId))
            );
            result.add(user);
        }
        return result;
    }
}
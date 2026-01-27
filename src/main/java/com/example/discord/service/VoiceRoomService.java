package com.example.discord.service;

import com.example.discord.dto.ChannelResponse;
import com.example.discord.dto.voice.VoiceRoom;
import com.example.discord.entity.Channel;
import com.example.discord.entity.Server;
import com.example.discord.repository.ChannelRepository;
import com.example.discord.repository.ServerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class VoiceRoomService {

    private final RedisTemplate<String, String> redis;
    private static final String KEY = "voice:channel:";

    public void join(Long serverId, Long channelId, String userId) {
        redis.opsForSet().add(KEY + serverId + ":" + channelId, userId);
    }

    public void leave(Long serverId, Long channelId, String userId) {
        redis.opsForSet().remove(KEY + serverId + ":" + channelId, userId);
    }
}

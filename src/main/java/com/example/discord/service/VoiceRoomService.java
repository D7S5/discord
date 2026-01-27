package com.example.discord.service;

import com.example.discord.dto.ChannelResponse;
import com.example.discord.dto.voice.VoiceRoom;
import com.example.discord.entity.Channel;
import com.example.discord.entity.Server;
import com.example.discord.repository.ChannelRepository;
import com.example.discord.repository.ServerRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class VoiceRoomService {

    private final ServerRepository serverRepository;
    private final ChannelRepository channelRepository;

    public ChannelResponse createRoom(Long serverId, String name) {
        Server server = serverRepository.findById(serverId).orElseThrow();
        Channel channel = Channel.voice(server, name);
        channelRepository.save(channel);
        return ChannelResponse.from(channel);
    }

//    public List<VoiceRoom> getRooms() {
//        return new ArrayList<>(rooms.values());
//    }
//
//    public VoiceRoom getRoom(String roomId) {
//        return rooms.get(roomId);
//    }
//
//    public void joinRoom(String roomId, String userId) {
//        VoiceRoom room = rooms.get(roomId);
//        if (room != null) {
//            room.join(userId);
//        }
//    }
//
//    public void leaveRoom(String roomId, String userId) {
//        VoiceRoom room = rooms.get(roomId);
//        if (room != null) {
//            room.leave(userId);
//        }
//    }
}

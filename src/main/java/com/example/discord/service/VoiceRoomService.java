package com.example.discord.service;

import com.example.discord.dto.voice.VoiceRoom;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VoiceRoomService {

    @Getter
    private final Map<String, VoiceRoom> rooms = new ConcurrentHashMap<>();

    public VoiceRoom createRoom(String name) {
        VoiceRoom room = new VoiceRoom(name);
        rooms.put(room.getId(), room);
        return room;
    }

    public List<VoiceRoom> getRooms() {
        return new ArrayList<>(rooms.values());
    }

    public VoiceRoom getRoom(String roomId) {
        return rooms.get(roomId);
    }

    public void joinRoom(String roomId, String userId) {
        VoiceRoom room = rooms.get(roomId);
        if (room != null) {
            room.join(userId);
        }
    }

    public void leaveRoom(String roomId, String userId) {
        VoiceRoom room = rooms.get(roomId);
        if (room != null) {
            room.leave(userId);
        }
    }
}

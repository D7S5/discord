package com.example.discord.controller;

import com.example.discord.dto.ChannelResponse;
import com.example.discord.dto.CreateChannelRequest;
import com.example.discord.dto.voice.VoiceRoom;
import com.example.discord.dto.voice.VoiceRoomResponse;
import com.example.discord.service.VoiceRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/channels/{serverId}/channels")
@RequiredArgsConstructor
public class VoiceRoomController {

    private final VoiceRoomService voiceRoomService;

//    @PostMapping("/voice")
//    public ChannelResponse createRoom(
//            @PathVariable Long serverId,
//            @RequestBody CreateChannelRequest req) {
//        return voiceRoomService.createRoom(serverId, req.getName());
//    }
//
//    @GetMapping
//    public List<VoiceRoomResponse> getRooms() {
//        return voiceRoomService.getRooms().stream()
//                .map(r -> new VoiceRoomResponse(r.getId(), r.getName()))
//                .collect(Collectors.toList());
//    }
//
//    @PostMapping("/{roomId}/join")
//    public void joinRoom(@PathVariable String roomId, @RequestParam String userId) {
//        voiceRoomService.joinRoom(roomId, userId);
//    }
//
//    @PostMapping("/{roomId}/leave")
//    public void leaveRoom(@PathVariable String roomId, @RequestParam String userId) {
//        voiceRoomService.leaveRoom(roomId, userId);
//    }
}

package com.example.discord.dto.voice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VoiceRoomResponse {
    private String roomId;
    private String name;
}

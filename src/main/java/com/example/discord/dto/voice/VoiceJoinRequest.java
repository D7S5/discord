package com.example.discord.dto.voice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class VoiceJoinRequest {
    private Long serverId;
    private Long channelId;
}

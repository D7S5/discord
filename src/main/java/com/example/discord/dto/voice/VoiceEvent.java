package com.example.discord.dto.voice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VoiceEvent {

    private String type;

    private Long channelId;

    private String userId;
}

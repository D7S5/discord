package com.example.discord.dto.voice;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VoiceSignalMessage {

    private String type;
    private String from;
    private String to;
    private Object data;
}

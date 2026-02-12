package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class VoiceDto {
    private String userId;
    private String username;
    private String iconUrl;
    private boolean speaking;
}

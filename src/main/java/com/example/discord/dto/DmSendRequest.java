package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DmSendRequest {
    private String roomId;
    private String content;
}

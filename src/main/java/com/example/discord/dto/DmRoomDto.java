package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class DmRoomDto {

    private String roomId;
    private String opponentId;
    private OffsetDateTime lastMessageAt;
}

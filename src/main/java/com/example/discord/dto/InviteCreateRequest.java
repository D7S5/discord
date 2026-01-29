package com.example.discord.dto;

import lombok.Getter;

@Getter
public class InviteCreateRequest {

    private Integer expireMinutes;
    private Integer maxUses;
}

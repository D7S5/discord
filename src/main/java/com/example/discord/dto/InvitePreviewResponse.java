package com.example.discord.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class InvitePreviewResponse {
    private Long serverId;
    private String serverName;
    private int memberCount;
}
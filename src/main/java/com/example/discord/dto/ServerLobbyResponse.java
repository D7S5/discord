package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ServerLobbyResponse {
    private ServerSummaryResponse server;
    private List<ChannelResponse> channels;
}

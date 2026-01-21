package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@ToString
public class ServerLobbyResponse {
    private ServerSummaryResponse server;
    private List<ChannelResponse> channels;
    private List<MemberStatusResponse> members;
}

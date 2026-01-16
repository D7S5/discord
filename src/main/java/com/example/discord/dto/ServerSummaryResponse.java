package com.example.discord.dto;

import com.example.discord.entity.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerSummaryResponse {
    private Long id;
    private String name;

    public static ServerSummaryResponse from(Server server) {
        return new ServerSummaryResponse(server.getId(), server.getName());
    }
}

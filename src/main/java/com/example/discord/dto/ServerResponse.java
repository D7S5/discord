package com.example.discord.dto;

import com.example.discord.entity.Server;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerResponse {
    private Long id;
    private String name;

    public static ServerResponse from(Server server) {
        return new ServerResponse(server.getId(), server.getName());
    }
}

package com.example.discord.dto;

import com.example.discord.entity.Server;
import com.example.discord.entity.ServerMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class ServerJoinResponse {

    private Long serverId;
    private String serverName;
    private String role;
    private OffsetDateTime joinedAt;

    public static ServerJoinResponse from(Server server, ServerMember member) {
        return new ServerJoinResponse(
                server.getId(),
                server.getName(),
                member.getRole().name(),
                member.getJoinedAt()
        );
    }
}


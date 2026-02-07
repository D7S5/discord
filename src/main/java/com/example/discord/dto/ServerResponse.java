package com.example.discord.dto;

import com.example.discord.entity.Role;
import com.example.discord.entity.Server;
import com.example.discord.entity.ServerMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ServerResponse {
    private Long id;
    private String name;
    private String iconUrl;
    private boolean isOwner;
    private String role;

    public static ServerResponse from(ServerMember member) {
        boolean isOwner = member.getRole() == Role.OWNER;

        return ServerResponse.builder()
                .id(member.getServer().getId())
                .name(member.getServer().getName())
                .iconUrl(member.getServer().getIconUrl())
                .role(member.getRole().name())
                .isOwner(isOwner)
                .build();
    }
}

package com.example.discord.dto;

import com.example.discord.entity.ServerMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {

    private Long userId;
    private String username;
    private String role;

    public static MemberResponse from(ServerMember member) {
        return new MemberResponse(
                member.getUser().getId(),
                member.getUser().getUsername(),
                member.getRole().name()
        );
    }
}

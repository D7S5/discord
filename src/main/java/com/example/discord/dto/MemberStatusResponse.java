package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberStatusResponse {
    private String userId;
    private String username;
    private String role;
    private boolean online;
}

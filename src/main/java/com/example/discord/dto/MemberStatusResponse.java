package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MemberStatusResponse {
    private String id;
    private String username;
    private boolean online;
}

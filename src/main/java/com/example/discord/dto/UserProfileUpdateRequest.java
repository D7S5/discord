package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserProfileUpdateRequest {
    private String username;
    private String status;
}

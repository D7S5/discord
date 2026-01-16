package com.example.discord.dto.register;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String email;
    private String username;
    private String password;
}

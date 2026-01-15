package com.example.discord.dto.register;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String email;
    private String password;
    private String username;
}

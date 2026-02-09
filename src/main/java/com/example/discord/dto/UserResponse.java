package com.example.discord.dto;

import com.example.discord.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private String id;
    private String username;
    private String iconUrl;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .iconUrl(user.getIconUrl())
                .build();
    }
}

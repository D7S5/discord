package com.example.discord.dto;

import com.example.discord.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private String profileImageUrl;

    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getUsername(),
                user.getIconUrl()
        );
    }
}


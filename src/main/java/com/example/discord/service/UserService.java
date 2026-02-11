package com.example.discord.service;

import com.example.discord.dto.UserResponse;
import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return UserResponse.from(user);
    }

    public void updateProfile(String userId, String username, String statusMessage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (username != null && !username.isBlank()) {
            if (username.length() > 32) {
                throw new IllegalArgumentException("닉네임 너무 깁니다");
            }
            user.setUsername(username);
        }
        user.setStatusMessage(
                statusMessage == null || statusMessage.isBlank()
                        ? null
                        : statusMessage);
    }
}

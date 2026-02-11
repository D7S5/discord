package com.example.discord.controller;

import com.example.discord.dto.UserProfileUpdateRequest;
import com.example.discord.dto.UserResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal UserPrincipal user) {
        return userService.getUser(user.getId());
    }

    @PatchMapping("/me/profile")
    public void saveProfile(@AuthenticationPrincipal UserPrincipal user,
                            @RequestBody UserProfileUpdateRequest request
                            ) {
        userService.updateProfile(user.getId(), request.getUsername(), request.getStatus());
    }
}

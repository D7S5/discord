package com.example.discord.controller;

import com.example.discord.dto.UserResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public UserResponse getUser(@PathVariable String id) {
        return userService.getUser(id);
    }

    @GetMapping("/me")
    public UserResponse getMe(@AuthenticationPrincipal UserPrincipal user) {
        return userService.getUser(user.getId());
    }
}

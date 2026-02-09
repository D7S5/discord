package com.example.discord.controller;

import com.example.discord.dto.UserProfileResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/me")
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/avatar")
    public UserProfileResponse uploadAvatar(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestPart MultipartFile image
    ) throws IOException {

        return profileService.updateAvatar(principal.getId(), image);
    }
}

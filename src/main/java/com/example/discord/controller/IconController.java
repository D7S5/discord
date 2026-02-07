package com.example.discord.controller;

import com.example.discord.dto.ServerResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IconController {

    private final ServerService serverService;

    @PutMapping("/channels/{serverId}/icon")
    public void updateIcon(
            @PathVariable Long serverId,
            @RequestPart("icon") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        serverService.updateServerIcon(serverId, user.getId(), file);
    }

    @DeleteMapping("/channels/{serverId}/icon")
    public void deleteServerIcon(
            @PathVariable Long serverId,
            @AuthenticationPrincipal UserPrincipal user
    ) throws IOException {
        serverService.deleteServerIcon(serverId, user.getId());
    }
}

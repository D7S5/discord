package com.example.discord.controller;

import com.example.discord.dto.CreateServerRequest;
import com.example.discord.dto.ServerLobbyResponse;
import com.example.discord.dto.ServerResponse;
import com.example.discord.security.AuthUtil;
import com.example.discord.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService service;

    @PostMapping
    public ServerResponse createServer(
            @RequestBody CreateServerRequest request,
            Authentication authentication
    ) {
        Long userId = AuthUtil.getUserId(authentication);

        return service.createServer(request, userId);
    }

    @GetMapping("/{serverId}/lobby")
    public ServerLobbyResponse lobby(
            @PathVariable Long serverId,
            Authentication authentication
    ) {
        Long userId = AuthUtil.getUserId(authentication);
        return service.getLobby(serverId, userId);
    }

    @GetMapping("/me")
    public Long me(Authentication authentication) {
        return AuthUtil.getUserId(authentication);
    }
}

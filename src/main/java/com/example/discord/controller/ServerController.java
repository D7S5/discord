package com.example.discord.controller;

import com.example.discord.dto.CreateServerRequest;
import com.example.discord.entity.Server;
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
    public ResponseEntity<?> createServer(
            @RequestBody CreateServerRequest request,
            Authentication authentication
    ) {
        Long userId = AuthUtil.getUserId(authentication);

        Server server = service.createServer(
                request.getName(),
                userId
        );
        return ResponseEntity.ok(server.getId());
    }

    @GetMapping("/me")
    public Long me(Authentication authentication) {
        return AuthUtil.getUserId(authentication);
    }
}

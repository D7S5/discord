package com.example.discord.controller;

import com.example.discord.dto.CreateServerRequest;
import com.example.discord.dto.ServerLobbyResponse;
import com.example.discord.dto.ServerResponse;
import com.example.discord.security.AuthUtil;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.ServerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/channels")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService service;

    @PostMapping
    public ServerResponse createServer(
            @RequestBody CreateServerRequest request,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        System.out.println("createServer = " + request + " userId = " + user.getId());
        return service.createServer(request, user.getId());
    }

    @GetMapping("/{serverId}/lobby")
    public ServerLobbyResponse lobby(
            @PathVariable Long serverId,
            @AuthenticationPrincipal UserPrincipal user
    ) {
        ServerLobbyResponse res = service.getLobby(serverId, user.getId());
        return res;
    }

    @GetMapping("/me")
    public List<ServerResponse> me
            (@AuthenticationPrincipal UserPrincipal user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        return service.getMyServers(user.getId());
    }
}

package com.example.discord.controller;

import com.example.discord.security.UserPrincipal;
import com.example.discord.service.ServerJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ServerJoinController {

    private final ServerJoinService service;

    @DeleteMapping("/channels/{serverId}/leave")
    public ResponseEntity<Void> leaveServer(
            @PathVariable Long serverId,
            @AuthenticationPrincipal UserPrincipal principal
            ) {
        service.leaveServer(serverId, principal.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/channels/{serverId}")
    public ResponseEntity<Void> deleteServer(
            @PathVariable Long serverId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        service.leaveServer(serverId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}

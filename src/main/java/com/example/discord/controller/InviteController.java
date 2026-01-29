package com.example.discord.controller;

import com.example.discord.dto.InviteCreateRequest;
import com.example.discord.dto.InviteResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.ServerJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/invites")
public class InviteController {

    private final ServerJoinService inviteService;

    @PostMapping("/servers/{serverId}")
    public InviteResponse createInvite(
            @PathVariable Long serverId,
            @RequestBody InviteCreateRequest request,
            @AuthenticationPrincipal UserPrincipal authUser
    ) {
        return inviteService.createInviteCode(
                serverId,
                authUser.getId(),
                request
        );
    }
}
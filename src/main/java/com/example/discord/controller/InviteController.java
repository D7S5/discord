package com.example.discord.controller;

import com.example.discord.dto.InviteCreateRequest;
import com.example.discord.dto.InvitePreviewResponse;
import com.example.discord.dto.InviteResponse;
import com.example.discord.dto.ServerJoinResponse;
import com.example.discord.entity.Invite;
import com.example.discord.entity.Server;
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

    @PostMapping("/channels/{serverId}")
    public InviteResponse createInvite(
            @PathVariable Long serverId,
            @RequestBody InviteCreateRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return inviteService.createInviteCode(
                serverId,
                principal.getId(),
                request
        );
    }
    @PostMapping("/{code}/join")
    public ServerJoinResponse joinServer(
            @PathVariable String code,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return inviteService.joinServer(code, principal.getId());
    }

    @GetMapping("/{code}")
    public InvitePreviewResponse preview(@PathVariable String code) {
        return inviteService.preview(code);
    }
}
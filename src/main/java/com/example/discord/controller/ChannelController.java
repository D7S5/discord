package com.example.discord.controller;

import com.example.discord.dto.ChannelResponse;
import com.example.discord.dto.CreateChannelRequest;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.ChannelService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels/{serverId}")
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/channels")
    public ChannelResponse createChannel(
            @PathVariable Long serverId,
            @RequestBody CreateChannelRequest request,
            @AuthenticationPrincipal UserPrincipal user
            ) {
        return channelService.createChannel(
                serverId,
                request.getName(),
                request.getType(),
                user.getId());
    }
}

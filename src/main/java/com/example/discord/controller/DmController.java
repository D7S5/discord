package com.example.discord.controller;

import com.example.discord.security.UserPrincipal;
import com.example.discord.service.DmRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dm")
public class DmController {

    private final DmRoomService dmRoomService;

    @PostMapping("/open/{friendUserId}")
    public String openDm(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable String friendUserId
            ) {
        System.out.println("friendUserId = " +  friendUserId);
        return dmRoomService.openOrCreate(user.getId(), friendUserId)
                .getId();
    }
}

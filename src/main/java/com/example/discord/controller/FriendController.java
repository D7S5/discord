package com.example.discord.controller;

import com.example.discord.dto.FriendDto;
import com.example.discord.dto.FriendRequestDto;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    @GetMapping
    public List<FriendDto> friends(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return friendService.getFriends(user.getId());
    }

    @PostMapping("/request")
    public void requestFriend(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestBody FriendRequestDto dto
            ) {
        friendService.sendRequest(user.getId(), dto.getUsername());
    }

    @PostMapping("/{id}/accept")
    public void acceptFriend(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable("id") String friendshipId
    ) {
        friendService.acceptRequest(user.getId(), friendshipId);
    }

    @GetMapping("/requests")
    public List<FriendDto> receivedRequest(
            @AuthenticationPrincipal UserPrincipal user
    ) {
        return friendService.getReceivedRequests(user.getId());
    }
}

package com.example.discord.controller;

import com.example.discord.dto.MemberStatusResponse;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final ServerMemberRepository serverMemberRepository;
    @GetMapping("/channels/{serverId}/members")
    public List<MemberStatusResponse> members(
            @PathVariable Long serverId) {

        return memberService.members(serverId);
    }
}
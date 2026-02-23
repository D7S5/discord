package com.example.discord.controller;

import com.example.discord.dto.MeResponse;
import com.example.discord.security.UserPrincipal;
import com.example.discord.service.MeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MeController {

    private final MeService meService;
    @GetMapping("/channels/@me")
    public MeResponse me(@AuthenticationPrincipal UserPrincipal principal){
        return meService.getMePage(principal.getId());
    }
}

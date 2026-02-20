package com.example.discord.controller;

import com.example.discord.service.TossService;
import com.example.discord.toss.TossConfirmRequest;
import com.example.discord.toss.TossConfirmResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/billing/toss")
public class TossController {
    private final TossService tossService;

    @PostMapping("/confirm")
    public ResponseEntity<TossConfirmResponse> confirm(@RequestBody TossConfirmRequest req) {
        return ResponseEntity.ok(tossService.confirm(req));
    }
}

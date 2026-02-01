package com.example.discord.controller;

import com.example.discord.dto.LoginRequest;
import com.example.discord.dto.TokenResponse;
import com.example.discord.dto.register.RegisterRequest;
import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import com.example.discord.security.JwtProvider;
import com.example.discord.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        TokenResponse res = authService.login(request);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String userId = authService.register(request);
        String token = jwtProvider.generateToken(userId);

        return ResponseEntity.ok(new TokenResponse(token));
    }
}
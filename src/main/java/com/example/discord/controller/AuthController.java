package com.example.discord.controller;

import com.example.discord.dto.LoginRequest;
import com.example.discord.dto.TokenResponse;
import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import com.example.discord.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        String token = jwtProvider.generateToken(user.getId());

        return ResponseEntity.ok(new TokenResponse(token));
    }
}

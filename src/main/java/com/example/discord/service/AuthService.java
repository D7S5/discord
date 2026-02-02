package com.example.discord.service;

import com.example.discord.dto.LoginRequest;
import com.example.discord.dto.TokenResponse;
import com.example.discord.dto.register.RegisterRequest;
import com.example.discord.entity.RefreshToken;
import com.example.discord.entity.User;
import com.example.discord.repository.RefreshTokenRepository;
import com.example.discord.repository.UserRepository;
import com.example.discord.security.JwtProvider;
import com.example.discord.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();


        String accessToken = jwtProvider.generateToken(principal.getId());
        String refreshToken = jwtProvider.generateRefreshToken(principal.getId());

        refreshTokenRepository.save(
                RefreshToken.builder()
                        .userId(principal.getId())
                        .token(refreshToken)
                        .expiryDate(OffsetDateTime.now().plusDays(7))
                        .build()
        );

        TokenResponse res = new TokenResponse(accessToken, refreshToken);

        return res;
    }

    @Transactional
    public String register(RegisterRequest request) {
        try {
            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            userRepository.save(user);
            return user.getId();

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("EMAIL_OR_USERNAME_ALREADY_EXISTS");
        }
    }

    @Transactional
    public TokenResponse refresh(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("INVALID_REFRESH_TOKEN");
        }

        String userId = jwtProvider.getUserId(refreshToken);

        RefreshToken saved = refreshTokenRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("REFRESH_NOT_FOUND"));

        if (!saved.getToken().equals(refreshToken)) {
            throw new RuntimeException("TOKEN_MISMATCH");
        }

        String newAccessToken = jwtProvider.generateToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}

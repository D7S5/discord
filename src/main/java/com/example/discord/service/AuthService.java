package com.example.discord.service;

import com.example.discord.dto.LoginRequest;
import com.example.discord.dto.LoginResponse;
import com.example.discord.dto.TokenResponse;
import com.example.discord.dto.register.RegisterRequest;
import com.example.discord.entity.RefreshToken;
import com.example.discord.entity.User;
import com.example.discord.repository.RefreshTokenRepository;
import com.example.discord.repository.UserRepository;
import com.example.discord.security.CookieUtil;
import com.example.discord.security.JwtProvider;
import com.example.discord.security.TokenHashUtil;
import com.example.discord.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final StringRedisTemplate redis;
    private final CookieUtil cookieUtil;

    private static final String REDIS_CURRENT_PREFIX = "RT:current:";
    private static final String REDIS_BLACKLIST_PREFIX = "RT:blacklist:";

    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshTokenExpiry;


    public LoginResponse login(LoginRequest request,
                               HttpServletResponse response) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(principal.getId());
        String refreshToken = jwtProvider.generateRefreshToken(principal.getId());

        String hashedRefreshToken = TokenHashUtil.hash(refreshToken);

        redis.opsForValue().set(
                REDIS_CURRENT_PREFIX + principal.getId(),
                hashedRefreshToken,
                jwtRefreshTokenExpiry,
                TimeUnit.MILLISECONDS
        );

//        log.info("LOGIN refresh hash={}", hashedRefreshToken);

        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        LoginResponse res = new LoginResponse(accessToken, principal.getId());

        return res;
    }

    @Transactional
    public void register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다");
            }
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }

            User user = User.builder()
                    .email(request.getEmail())
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw new IllegalStateException("EMAIL_OR_USERNAME_ALREADY_EXISTS");
        }
    }

    @Transactional
    public TokenResponse refresh(HttpServletRequest request,
                                 HttpServletResponse response) {

        String oldRefreshToken = jwtProvider.getRefreshToken(request);

        if ( oldRefreshToken == null || oldRefreshToken.isBlank() ) {
            throw new RuntimeException("Refresh Token missing");
        }

        if ( !jwtProvider.validateRefreshToken(oldRefreshToken)) {
//            log.warn("oldRefreshToken = " + oldRefreshToken);
            throw new RuntimeException("invalid Refresh Token");
        }

        String oldHashToken = TokenHashUtil.hash(oldRefreshToken);
        String userId = jwtProvider.getUserId(oldRefreshToken);
        String key = REDIS_CURRENT_PREFIX + userId;

        // reuse 감지
        if (redis.hasKey(REDIS_BLACKLIST_PREFIX + oldHashToken)) {
            redis.delete(key);
            cookieUtil.clearRefreshTokenCookie(response);
            throw new SecurityException("Refresh Token Reuse Detected");
        }

        String savedHash = redis.opsForValue().get(key);

        if (!oldHashToken.equals(savedHash)) {
            redis.delete(key);
            cookieUtil.clearRefreshTokenCookie(response);
            throw new SecurityException("Refresh token Mismatch");
        }

        long ttl = Math.max(remainingTTL(oldRefreshToken), 1000);

        redis.opsForValue().set(
                REDIS_BLACKLIST_PREFIX + oldHashToken,
                "USED",
                ttl,
                TimeUnit.MILLISECONDS
        );

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);
        String hashedRefreshToken = TokenHashUtil.hash(newRefreshToken);

        redis.opsForValue().set(
                REDIS_CURRENT_PREFIX + userId,
                hashedRefreshToken,
                jwtRefreshTokenExpiry,
                TimeUnit.MILLISECONDS
        );

        cookieUtil.addRefreshTokenCookie(response, newRefreshToken);

        return new TokenResponse(newAccessToken);
    }

    private long remainingTTL(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtProvider.getKey())
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            long expirationTime = claims.getExpiration().getTime();
            long now = System.currentTimeMillis();

            return Math.max(expirationTime - now, 0);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return 0;
        }
    }
}
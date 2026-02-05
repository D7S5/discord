package com.example.discord.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
@Getter
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretKey;

    private Key key;

    @Value("${jwt.access-expiration}")
    private long accessExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    private static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(
                secretKey.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateAccessToken(String userId) {
        return Jwts.builder()
                .setSubject(userId) // ⭐ userId
                .claim("type", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }
//    public boolean validateAccessToken(String token) {
//        try {
//            Claims claims = parseClaims(token);
//
//            if (claims.getExpiration().before(new Date())) return false;
//            if (!"ACCESS".equals(claims.get("type", String.class))) return false;
//
//            return true;
//        } catch (JwtException e) {
//            return false;
//        }
//    }
    public boolean validateAccessToken(String token) {
        try {
            Claims claims = parseClaims(token);

            log.info("JWT claims = {}", claims);

            Date exp = claims.getExpiration();
            log.info("exp = {}, now = {}", exp, new Date());

            String type = claims.get("type", String.class);
            log.info("type = {}", type);

            if (exp == null || exp.before(new Date())) {
                log.warn("❌ token expired");
                return false;
            }

            if (!"ACCESS".equals(type)) {
                log.warn("❌ token type mismatch");
                return false;
            }

            return true;

        } catch (Exception e) {
            log.error("❌ validateAccessToken exception", e);
            return false;
        }
    }
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = parseClaims(token);

            // 1. 만료 여부
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                return false;
            }

            // 2. 타입 확인
            String type = claims.get("type", String.class);
            if (!"REFRESH".equals(type)) {
                return false;
            }

            // 3. subject(userId) 존재 여부
            String userId = claims.getSubject();
            if (userId == null || userId.isBlank()) {
                return false;
            }

            return true;

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getUserId(String token) {
        return
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
    }

    public String getRefreshToken(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if (REFRESH_TOKEN_COOKIE.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
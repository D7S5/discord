package com.example.discord.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final CookieUtil cookieUtil;
    private final StringRedisTemplate redis;

    private static final String REDIS_CURRENT_PREFIX = "RT:current:";
    @Value("${jwt.refresh-expiration}")
    private long jwtRefreshTokenExpiry;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        CustomUserPrincipal principal =
                (CustomUserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(principal.getUser().getId());
        String refreshToken = jwtProvider.generateRefreshToken(principal.getUser().getId());
        String hashed = TokenHashUtil.hash(refreshToken);

        String redirect = request.getParameter("redirect");
        if (redirect == null || redirect.isBlank()) redirect = "/channels";

        redis.opsForValue().set(
                REDIS_CURRENT_PREFIX + principal.getUser().getId(),
                hashed,
                jwtRefreshTokenExpiry,
                TimeUnit.MILLISECONDS
        );

        cookieUtil.addRefreshTokenCookie(response, refreshToken);

        response.sendRedirect(
                "http://localhost:3000/oauth-success"
                        + "?token=" + accessToken
                        + "&userId=" + principal.getUser().getId()
                        + "&redirect=" + java.net.URLEncoder.encode(redirect, java.nio.charset.StandardCharsets.UTF_8)
        );
    }
}
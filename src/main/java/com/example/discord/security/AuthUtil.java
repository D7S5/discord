package com.example.discord.security;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class AuthUtil {

    public static Long getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("UNAUTHENTICATED");
        }
        return Long.parseLong(authentication.getName());
    }

    public static Long getCurrentUserId() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("UNAUTHENTICATED");
        }

        return Long.parseLong(authentication.getName());
    }
}

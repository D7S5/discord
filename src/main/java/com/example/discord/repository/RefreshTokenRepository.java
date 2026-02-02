package com.example.discord.repository;

import com.example.discord.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    void deleteByUserId(String id);

    Optional<RefreshToken> findByToken(String oldRefreshToken);
}

package com.example.discord.repository;

import com.example.discord.entity.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Optional<Invite> findByCode(String code);

    boolean existsByCode(String code);
}

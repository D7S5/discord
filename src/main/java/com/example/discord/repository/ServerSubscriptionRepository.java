package com.example.discord.repository;

import com.example.discord.toss.ServerSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ServerSubscriptionRepository extends JpaRepository<ServerSubscription, Long> {
    Optional<ServerSubscription> findByServerId(Long serverId);
}

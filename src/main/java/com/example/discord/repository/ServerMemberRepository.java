package com.example.discord.repository;

import com.example.discord.entity.ServerMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {

    Optional<ServerMember> findByServerIdAndUserId(Long serverId, String userId);

    boolean existsByServerIdAndUserId(Long serverId, String userId);

    List<ServerMember> findByServerId(Long serverId);
}


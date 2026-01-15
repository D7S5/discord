package com.example.discord.repository;

import com.example.discord.entity.ServerMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<ServerMember, Long> {
}

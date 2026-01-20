package com.example.discord.repository;

import com.example.discord.entity.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ServerRepository extends JpaRepository<Server, Long> {
    @Query("""
        select s from Server s
        join s.members m
        where m.user.id = :userId
    """)
    List<Server> findByMemberUserId(String userId);
}

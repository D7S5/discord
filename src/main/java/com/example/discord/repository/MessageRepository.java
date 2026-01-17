package com.example.discord.repository;

import com.example.discord.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("""
        select m from Message m
        join fetch m.sender
        where m.channel.id = :channelId
        order by m.createdAt asc
    """)
    List<Message> findByChannelId(Long channelId);
}

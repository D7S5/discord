package com.example.discord.repository;

import com.example.discord.dto.ChannelResponse;
import com.example.discord.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChannelRepository extends JpaRepository<Channel, Long> {
    List<Channel> findByServerIdOrderByIdAsc(Long serverId);

    void deleteByServerId(Long serverId);
}

package com.example.discord.repository;

import com.example.discord.entity.DmMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DmMessageRepository extends JpaRepository<DmMessage, Long> {
    List<DmMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);
}

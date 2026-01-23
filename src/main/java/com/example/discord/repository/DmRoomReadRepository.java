package com.example.discord.repository;

import com.example.discord.entity.DmRoomRead;
import com.example.discord.entity.DmRoomReadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DmRoomReadRepository extends JpaRepository<DmRoomRead, DmRoomReadId> {

    Optional<DmRoomRead> findByRoomIdAndUserId(String roomId, String userId);
}

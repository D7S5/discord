package com.example.discord.repository;

import com.example.discord.entity.DmRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DmRoomRepository extends JpaRepository<DmRoom, String> {
    @Query("""
        select d from DmRoom d
        where d.userA = :userId or d.userB = :userId
        order by d.lastMessageAt desc nulls last
    """)
    List<DmRoom> findDmRooms(@Param("userId") String userId);
}

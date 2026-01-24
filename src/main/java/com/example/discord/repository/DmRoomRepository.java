package com.example.discord.repository;

import com.example.discord.entity.DmRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DmRoomRepository extends JpaRepository<DmRoom, String> {
    @Query("""
        select d from DmRoom d
        where d.userA.id = :userId or d.userB.id = :userId
        order by d.lastMessageAt desc nulls last
    """)
    List<DmRoom> findDmRooms(@Param("userId") String userId);

    @Query("""
        select d from DmRoom d
        where d.userA.id = :a and d.userB.id = :b
    """)
    Optional<DmRoom> findRoom(
            @Param("a") String a,
            @Param("b") String b
    );
}

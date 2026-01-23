package com.example.discord.repository;

import com.example.discord.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friendship, String> {

    @Query("""
        select f from Friendship f
        where f.status = 'FRIENDS'
        and (f.user.id = :userId or f.target.id = :userId)
    """)
    List<Friendship> findFriends(@Param("userId") String userId);
}

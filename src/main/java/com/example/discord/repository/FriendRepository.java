package com.example.discord.repository;

import com.example.discord.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRepository extends JpaRepository<Friendship, String> {

    @Query("""
        select f from Friendship f
        where f.status = 'ACCEPTED'
        and (f.user.id = :userId or f.target.id = :userId)
    """)
    List<Friendship> findFriends(@Param("userId") String userId);

    @Query("""
        select count(f) > 0 from Friendship f
        where (f.user.id = :a and f.target.id = :b)
           or (f.user.id = :b and f.target.id = :a)
    """)
    boolean existsBetween(@Param("a") String a, @Param("b") String b);

    @Query("""
    select f from Friendship f
    where f.status = 'PENDING'
      and f.target.id = :userId
""")
    List<Friendship> findReceivedRequests(@Param("userId") String userId);
}

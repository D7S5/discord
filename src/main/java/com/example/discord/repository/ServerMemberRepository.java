package com.example.discord.repository;

import com.example.discord.entity.Role;
import com.example.discord.entity.Server;
import com.example.discord.entity.ServerMember;
import com.example.discord.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ServerMemberRepository extends JpaRepository<ServerMember, Long> {

    Optional<ServerMember> findByServerIdAndUserId(Long serverId, String userId);

    boolean existsByServerIdAndUserId(Long serverId, String userId);
    boolean existsByServerIdAndUserIdAndRoleIn(Long serverId, String userId, Collection<Role> roles);
    List<ServerMember> findByServerId(Long serverId);

    boolean existsByServerAndUser(Server server, User user);

    @Query("""
    select sm.role
    from ServerMember sm
    where sm.server.id = :serverId
      and sm.user.id = :userId
""")
    Optional<Role> findRoleByServerIdAndUserId(@Param("serverId") Long serverId, @Param("userId") String userId);

    @Query("""
        select sm
        from ServerMember sm
        join fetch sm.server s
        where sm.user.id = :userId
    """)
    List<ServerMember> findMyServers(@Param("userId") String userId);
}


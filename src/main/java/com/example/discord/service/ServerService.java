package com.example.discord.service;

import com.example.discord.entity.Role;
import com.example.discord.entity.Server;
import com.example.discord.entity.ServerMember;
import com.example.discord.entity.User;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.repository.ServerRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;
    private final UserRepository userRepository;

    public Server createServer(String name, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow();

        Server server = serverRepository.save(
                new Server(name, user.getId())
        );

        memberRepository.save(
                new ServerMember(server, user, Role.OWNER)
        );
        return server;
    }

    public void deleteServer(Long serverId, Long userId) {
        ServerMember member = memberRepository
                .findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new AccessDeniedException("NOT_MEMBER"));

        if (member.getRole() != Role.OWNER) {
            throw new AccessDeniedException("ONLY_OWNER");
        }

        serverRepository.deleteById(serverId);
    }
}

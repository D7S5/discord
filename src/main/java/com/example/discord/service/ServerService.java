package com.example.discord.service;

import com.example.discord.entity.Role;
import com.example.discord.entity.Server;
import com.example.discord.entity.ServerMember;
import com.example.discord.entity.User;
import com.example.discord.repository.MemberRepository;
import com.example.discord.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final MemberRepository memberRepository;

    public Server createServer(String name, User user) {
        Server server = serverRepository.save(
                new Server(name, user.getId())
        );

        memberRepository.save(
                new ServerMember(server, user, Role.OWNER)
        );
        return server;
    }
}

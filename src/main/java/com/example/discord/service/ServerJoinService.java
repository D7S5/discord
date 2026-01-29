package com.example.discord.service;

import com.example.discord.dto.ServerJoinResponse;
import com.example.discord.entity.*;
import com.example.discord.repository.InviteRepository;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class ServerJoinService {

    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final ServerMemberRepository serverMemberRepository;

    @Transactional
    public ServerJoinResponse joinServer(String inviteCode, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Invite invite = inviteRepository.findByCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_INVITE"));

        if (invite.isExpired()) {
            throw new IllegalStateException("INVITE_EXPIRED");
        }

        Server server = invite.getServer();

        if (serverMemberRepository.existsByServerAndUser(server, user)) {
            throw new IllegalStateException("ALREADY_JOINED");
        }

        ServerMember member = new ServerMember(
                server,
                user,
                Role.MEMBER,
                OffsetDateTime.now()
        );
        serverMemberRepository.save(member);

        // 4. 초대 사용 횟수 증가
        invite.increaseUseCount();

        return ServerJoinResponse.from(server, member);
    }
}

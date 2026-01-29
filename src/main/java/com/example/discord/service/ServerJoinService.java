package com.example.discord.service;

import com.example.discord.dto.InviteCreateRequest;
import com.example.discord.dto.InvitePreviewResponse;
import com.example.discord.dto.InviteResponse;
import com.example.discord.dto.ServerJoinResponse;
import com.example.discord.entity.*;
import com.example.discord.repository.InviteRepository;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.repository.ServerRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class ServerJoinService {

    private final UserRepository userRepository;
    private final InviteRepository inviteRepository;
    private final ServerMemberRepository serverMemberRepository;
    private final ServerRepository serverRepository;

    public InviteResponse createInviteCode(
            Long serverId,
            String userId,
            InviteCreateRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("SERVER_NOT_FOUND"));

        // (선택) 권한 체크
//        if (!server.isAdmin(user)) {
//            throw new IllegalStateException("NO_PERMISSION");
//        }

        OffsetDateTime expiresAt = null;
        if (request.getExpireMinutes() != null) {
            expiresAt = OffsetDateTime.now()
                    .plusMinutes(request.getExpireMinutes());
        }

        String inviteCode = generateInviteCode();

        Invite invite = new Invite(
                server,
                user,
                expiresAt,
                request.getMaxUses(),
                inviteCode
        );

        inviteRepository.save(invite);
        return InviteResponse.from(invite);
    }

    // 🔑 초대 코드 생성 로직
    private String generateInviteCode() {
        String code;
        do {
            code = UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 8);
        } while (inviteRepository.existsByCode(code));
        return code;
    }

    @Transactional
    public ServerJoinResponse joinServer(String inviteCode, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Invite invite = inviteRepository.findByCode(inviteCode)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_INVITE"));

        if (invite.isExpired()) {
            throw new IllegalStateException("INVITE_EXPIRED");
        }

        if (invite.isMaxUsed()) {
            throw new IllegalStateException("INVITE_MAX_USED");
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

    public InvitePreviewResponse preview(String code) {
        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("INVALID_INVITE"));

        if (invite.isExpired()) {
            throw new IllegalStateException("INVITE_EXPIRED");
        }

        System.out.println("Invite code = " + code);

        Server server = invite.getServer();

        return InvitePreviewResponse.builder()
                .serverId(server.getId())
                .serverName(server.getName())
                .memberCount(server.getMembers().size())
                .build();
    }
}

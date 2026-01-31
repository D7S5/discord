package com.example.discord.service;

import com.example.discord.entity.Role;
import com.example.discord.entity.ServerMember;
import com.example.discord.repository.ServerMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.discord.entity.Role.ADMIN;
import static com.example.discord.entity.Role.MEMBER;

@Transactional
@Service
@RequiredArgsConstructor
public class RoleService {

    private final ServerMemberRepository memberRepository;
    @Transactional
    public void changeRole(
            Long serverId,
            String targetUserId,
            String requesterId
    ) {
        // 요청자
        ServerMember requester = memberRepository
                .findByServerIdAndUserId(serverId, requesterId)
                .orElseThrow(() -> new IllegalArgumentException("NOT_A_MEMBER"));

        // 대상
        ServerMember target = memberRepository
                .findByServerIdAndUserId(serverId, targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("TARGET_NOT_FOUND"));

        // 🔐 권한 규칙
        if (requester.getRole() != Role.OWNER) {
            throw new SecurityException("ONLY_OWNER_CAN_CHANGE_ROLE");
        }

        if (target.getRole() == Role.OWNER) {
            throw new IllegalArgumentException("CANNOT_CHANGE_OWNER_ROLE");
        }

//        if (newRole == Role.OWNER) {
//            throw new IllegalArgumentException("USE_TRANSFER_OWNER_API");
//        }

        target.setRole(
                target.getRole() == ADMIN ? MEMBER : ADMIN
        );
    }
}

package com.example.discord.service;

import com.example.discord.dto.MemberStatusResponse;
import com.example.discord.entity.ServerMember;
import com.example.discord.redis.PresenceService;
import com.example.discord.repository.ServerMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class MemberService {

    private final ServerMemberRepository serverMemberRepository;
    private final PresenceService presenceService;

    public List<MemberStatusResponse> members(Long serverId) {
        List<ServerMember> members =
                serverMemberRepository.findByServerId(serverId);

        Set<String> onlineUserIds =
                presenceService.onlineUsers(serverId);

        return members.stream()
                .map(m -> new MemberStatusResponse(
                        m.getUser().getId(),
                        m.getUser().getUsername(),
                        onlineUserIds.contains(m.getUser().getId())
                )).toList();
    }
}

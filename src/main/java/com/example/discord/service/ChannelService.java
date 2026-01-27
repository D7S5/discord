package com.example.discord.service;

import com.example.discord.dto.ChannelResponse;
import com.example.discord.entity.*;
import com.example.discord.repository.ChannelRepository;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ChannelService {

    private final ChannelRepository channelRepository;
    private final ServerRepository serverRepository;
    private final ServerMemberRepository serverMemberRepository;

    public ChannelResponse createChannel(
            Long serverId,
            String name,
            ChannelType type,
            String userId
    ) {
        ServerMember member = serverMemberRepository
                .findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new AccessDeniedException("NOT_MEMBER"));

        if (member.getRole() == Role.MEMBER) {
            throw new AccessDeniedException("NO_PERMISSION");
        }

        Server server = serverRepository.findById(serverId)
                .orElseThrow();

        if (type == ChannelType.VOICE) {
            Channel channel = Channel.voice(server, name);
            channelRepository.save(channel);
            return ChannelResponse.from(channel);
        }

        Channel channel = new Channel(server, name, type);
        channelRepository.save(channel);

        return ChannelResponse.from(channel);
    }
}

package com.example.discord.service;

import com.example.discord.dto.*;
import com.example.discord.entity.*;
import com.example.discord.repository.ChannelRepository;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.repository.ServerRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    public ServerResponse createServer(CreateServerRequest request, Long userId) {

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        Server server = new Server(request.getName(), owner);

        serverRepository.save(server);

        createDefaultChannels(server);

        return ServerResponse.from(server);
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

    private void createDefaultChannels(Server server) {
        Channel general = new Channel(server, "general", ChannelType.TEXT);
        Channel random = new Channel(server, "random", ChannelType.TEXT);

        channelRepository.save(general);
        channelRepository.save(random);
    }

    public ServerLobbyResponse getLobby(Long serverId, Long userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("SERVER_NOT_FOUND"));

        boolean isMember = memberRepository.existsByServerIdAndUserId(serverId, userId);
        if ( !isMember) {
            //채널 가입
            throw new IllegalStateException("NOT_A_SERVER_MEMBER");
        }

        List<ChannelResponse> channels = channelRepository.findByServerIdOrderByIdAsc(serverId)
                .stream()
                .map(ChannelResponse::from)
                .toList();

        return new ServerLobbyResponse(
                ServerSummaryResponse.from(server),
                channels
        );
    }

    public List<ServerResponse> getMyServers(Long userId) {
        return serverRepository.findByMemberUserId(userId)
                .stream()
                .map(ServerResponse::from)
                .toList();
    }
}

package com.example.discord.service;

import com.example.discord.dto.*;
import com.example.discord.entity.*;
import com.example.discord.redis.PresenceService;
import com.example.discord.repository.ChannelRepository;
import com.example.discord.repository.ServerMemberRepository;
import com.example.discord.repository.ServerRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final PresenceService presenceService;

    @Transactional
    public ServerResponse createServer(CreateServerRequest request, String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("USER_NOT_FOUND"));

        Server server = new Server(request.getName(), user);

        serverRepository.save(server);

        ServerMember owner = new ServerMember(
                server,
                user,
                Role.OWNER,
                OffsetDateTime.now()
        );

        memberRepository.save(owner);

        createDefaultChannels(server);

        return ServerResponse.from(owner);
    }

    public List<ServerListResponse> getMyServers(String userId) {
        return memberRepository.findMyServers(userId)
                .stream()
                .map(sm -> {
                    Server server = sm.getServer();

                    boolean isOwner =
                            server.getOwner().getId().equals(userId);

                    return new ServerListResponse(
                            server.getId(),
                            server.getName(),
//                            server.getIconUrl(),
                            isOwner,
                            sm.getRole().name() // OWNER / ADMIN / MEMBER
                    );
                })
                .toList();
    }

    public void deleteServer(Long serverId, String userId) {
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

    public ServerLobbyResponse getLobby(Long serverId, String userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new IllegalArgumentException("SERVER_NOT_FOUND"));

        boolean isMember = memberRepository.existsByServerIdAndUserId(serverId, userId);
        if (!isMember) {
            //채널 가입
            throw new IllegalStateException("NOT_A_SERVER_MEMBER");
        }

        List<ChannelResponse> channels = channelRepository.findByServerIdOrderByIdAsc(serverId)
                .stream()
                .map(ChannelResponse::from)
                .toList();


        List<ServerMember> members =
                memberRepository.findByServerId(serverId);

        Set<String> onlineUserIds =
                presenceService.onlineUsers(serverId);

        List<MemberStatusResponse> memberlist = members.stream()
                .map(m -> new MemberStatusResponse(
                        m.getUser().getId(),
                        m.getUser().getUsername(),
                        m.getRole().name(),
                        onlineUserIds.contains(m.getUser().getId())
                )).toList();

        Role myRole = memberRepository
                .findRoleByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new AccessDeniedException("NOT_A_MEMBER"));

        return new ServerLobbyResponse(
                ServerSummaryResponse.from(server),
                channels,
                memberlist,
                myRole
        );
    }

    @Transactional
    public void updateServerIcon(
            Long serverId,
            String userId,
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일이 없습니다");
        }
        ServerMember member = memberRepository
                .findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new SecurityException("서버 멤버 아님"));

        if (!member.isOwner()) {
            throw new SecurityException("OWNER만 아이콘 수정 가능");
        }

        Server server = member.getServer();

        try {
            String dirPath = "uploads/server/" + serverId;
            Files.createDirectories(Paths.get(dirPath));

            String filePath = dirPath + "/icon.png";

            Thumbnails.of(file.getInputStream())
                    .size(48, 48)
                    .outputFormat("png")
                    .outputQuality(1.0)
                    .toFile(filePath);

            file.transferTo(Paths.get(filePath));

            server.setIconUrl("/uploads/server/" + serverId + "/icon.png");

        } catch (IOException e) {
            throw new RuntimeException("아이콘 업로드 실패", e);
        }
    }


    @Transactional
    public void deleteServerIcon(Long serverId, String userId) throws IOException {

        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        if (!server.isOwner(userId)) {
            throw new SecurityException("No permission");
        }

        if (server.getIconUrl() != null &&
                !server.getIconUrl().contains("default")) {

            Path path = Paths.get(
                    server.getIconUrl().replace("/uploads/", "uploads/")
            );
            Files.deleteIfExists(path);
        }

        server.setIconUrl("/uploads/server/default.png");
    }

    public List<ServerResponse> getServers(String userId) {
        return memberRepository.findByUserId(userId)
                .stream()
                .map(ServerResponse::from)
                .toList();

    }

    public ServerResponse getServer(Long serverId, String userId) {

        ServerMember member = memberRepository
                .findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new AccessDeniedException("서버 멤버가 아님"));

        return ServerResponse.from(member);
    }
}

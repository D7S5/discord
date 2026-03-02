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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServerService {

    private final ServerRepository serverRepository;
    private final ServerMemberRepository memberRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final PresenceService presenceService;
    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    @Value("${cloud.aws.default-server-icon-url}")
    private String defaultServerIconUrl;

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
                    boolean isOwner = server.getOwner().getId().equals(userId);

                    return new ServerListResponse(
                            server.getId(),
                            server.getName(),
                            server.getIconUrl(),
                            isOwner,
                            sm.getRole().name()
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
            throw new IllegalStateException("NOT_A_SERVER_MEMBER");
        }

        List<ChannelResponse> channels = channelRepository.findByServerIdOrderByIdAsc(serverId)
                .stream()
                .map(ChannelResponse::from)
                .toList();

        List<ServerMember> members = memberRepository.findByServerId(serverId);
        Set<String> onlineUserIds = presenceService.onlineUsers(serverId);

        List<MemberStatusResponse> memberlist = members.stream()
                .map(m -> new MemberStatusResponse(
                        m.getUser().getId(),
                        m.getUser().getUsername(),
                        m.getRole().name(),
                        m.getUser().getIconUrl(),
                        onlineUserIds.contains(m.getUser().getId())
                ))
                .toList();

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
    public void updateServerIcon(Long serverId, String userId, MultipartFile file) {
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

        String oldIconKey = resolveOldIconKey(server);

        try {
            String newKey = "server/" + serverId + "/" + UUID.randomUUID() + ".png";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(file.getInputStream())
                    .size(48, 48)
                    .outputFormat("png")
                    .outputQuality(1.0)
                    .toOutputStream(outputStream);

            byte[] imageBytes = outputStream.toByteArray();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(newKey)
                    .contentType("image/png")
                    .build();

            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(imageBytes)
            );

            String imageUrl = String.format(
                    "https://%s.s3.%s.amazonaws.com/%s",
                    bucket,
                    region,
                    newKey
            );

            server.setIconUrl(imageUrl);
            server.setIconKey(newKey);

            if (oldIconKey != null && !oldIconKey.isBlank()) {
                try {
                    s3Client.deleteObject(
                            DeleteObjectRequest.builder()
                                    .bucket(bucket)
                                    .key(oldIconKey)
                                    .build()
                    );

                } catch (Exception e) {
                    log.warn("기존 서버 아이콘 삭제 실패. serverId={}, oldKey={}", serverId, oldIconKey, e);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("아이콘 업로드 실패", e);
        }
    }

    // 예전 키 대응
    private String resolveOldIconKey(Server server) {
        if (server.getIconKey() != null && !server.getIconKey().isBlank()) {
            return server.getIconKey();
        }

        String iconUrl = server.getIconUrl();
        if (iconUrl == null || iconUrl.isBlank()) {
            return null;
        }

        String prefix = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);

        if (iconUrl.startsWith(prefix)) {
            return iconUrl.substring(prefix.length());
        }

        return null;
    }

    @Transactional
    public void deleteServerIcon(Long serverId, String userId) {
        Server server = serverRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("Server not found"));

        if (!server.isOwner(userId)) {
            throw new SecurityException("No permission");
        }

        try {
            if (server.getIconKey() != null && !server.getIconKey().isBlank()) {
                s3Client.deleteObject(
                        DeleteObjectRequest.builder()
                                .bucket(bucket)
                                .key(server.getIconKey())
                                .build()
                );
            }

        } catch (Exception e) {
            log.warn("S3 서버 아이콘 삭제 실패. serverId={}", serverId, e);
        }

        server.setIconUrl(defaultServerIconUrl);
        server.setIconKey(null);
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

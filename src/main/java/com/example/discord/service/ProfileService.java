package com.example.discord.service;

import com.example.discord.dto.UserProfileResponse;
import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional
    public UserProfileResponse updateAvatar(String userId, MultipartFile image)
            throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow();

        // 기존 파일 삭제
        if (user.getIconUrl() != null &&
                !user.getIconUrl().contains("default-avatar.png")) {

            String oldFilename = Paths.get(user.getIconUrl())
                    .getFileName()
                    .toString();

            Path oldDir = Paths.get("uploads/avatar");
            Path oldPath = oldDir.resolve(oldFilename);
            Files.deleteIfExists(oldPath);
        }

        // 2️⃣ 확장자 추출
        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());

        // 3️⃣ UUID 파일명 생성
        String filename = java.util.UUID.randomUUID() + "." + ext;

        Path dir = Paths.get("uploads/avatar");
        Files.createDirectories(dir);

        Path path = dir.resolve(filename);
        Files.write(path, image.getBytes());

        // 4️⃣ DB 저장 경로
        String imageUrl = "/images/avatar/" + filename;
        user.setIconUrl(imageUrl);

        return UserProfileResponse.from(user);
    }


    @Transactional
    public UserProfileResponse deleteAvatar(String userId) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow();

        String iconUrl = user.getIconUrl();

        if (iconUrl != null && !iconUrl.isBlank()
            && !iconUrl.contains("default-avatar.png")) {

            String filename = Paths.get(iconUrl).getFileName().toString();

            Path filePath = Paths.get("uploads/avatar").resolve(filename);

            Files.deleteIfExists(filePath);
        }

        user.setIconUrl(null);

        return UserProfileResponse.from(user);
    }
}


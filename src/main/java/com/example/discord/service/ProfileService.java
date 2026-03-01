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

import com.example.discord.dto.UserProfileResponse;
import com.example.discord.entity.User;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

    @Transactional
    public UserProfileResponse updateAvatar(String userId, MultipartFile image) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        validateImage(image);

        // 기존 S3 이미지 삭제
        if (user.getIconKey() != null && !user.getIconKey().isBlank()) {
            s3Service.delete(user.getIconKey());
        }

        String imageUrl = s3Service.upload(image, "avatar");
        String imageKey = extractKeyFromUrl(imageUrl);

        user.setIconUrl(imageUrl);
        user.setIconKey(imageKey);

        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse deleteAvatar(String userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (user.getIconKey() != null && !user.getIconKey().isBlank()) {
            s3Service.delete(user.getIconKey());
        }

        user.setIconUrl(null);
        user.setIconKey(null);

        return UserProfileResponse.from(user);
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어 있습니다.");
        }

        if (image.getContentType() == null || !image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }

        if (image.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 10MB 이하여야 합니다.");
        }
    }

    private String extractKeyFromUrl(String url) {
        int idx = url.indexOf(".amazonaws.com/");
        if (idx == -1) {
            throw new IllegalArgumentException("유효한 S3 URL이 아닙니다.");
        }
        return url.substring(idx + ".amazonaws.com/".length());
    }
}
//    @Transactional
//    public UserProfileResponse updateAvatar(String userId, MultipartFile image)
//            throws IOException {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow();
//
//        // 기존 파일 삭제
//        if (user.getIconUrl() != null &&
//                !user.getIconUrl().contains("default-avatar.png")) {
//
//            String oldFilename = Paths.get(user.getIconUrl())
//                    .getFileName()
//                    .toString();
//
//            Path oldDir = Paths.get("uploads/avatar");
//            Path oldPath = oldDir.resolve(oldFilename);
//            Files.deleteIfExists(oldPath);
//        }
//
//        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
//
//
//        String filename = java.util.UUID.randomUUID() + "." + ext;
//
//        Path dir = Paths.get("uploads/avatar");
//        Files.createDirectories(dir);
//
//        Path path = dir.resolve(filename);
//        Files.write(path, image.getBytes());
//
//        String imageUrl = "/images/avatar/" + filename;
//        user.setIconUrl(imageUrl);
//
//        return UserProfileResponse.from(user);
//    }
//
//
//    @Transactional
//    public UserProfileResponse deleteAvatar(String userId) throws IOException {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow();
//
//        String iconUrl = user.getIconUrl();
//
//        if (iconUrl != null && !iconUrl.isBlank()
//            && !iconUrl.contains("default-avatar.png")) {
//
//            String filename = Paths.get(iconUrl).getFileName().toString();
//
//            Path filePath = Paths.get("uploads/avatar").resolve(filename);
//
//            Files.deleteIfExists(filePath);
//        }
//
//        user.setIconUrl(null);
//
//        return UserProfileResponse.from(user);
//    }
//    }
//

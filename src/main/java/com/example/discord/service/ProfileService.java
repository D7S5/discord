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

        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String filename = userId + "." + ext;

        Path dir = Paths.get("uploads/avatar");
        Files.createDirectories(dir);

        Path path = dir.resolve(filename);
        Files.write(path, image.getBytes());

        String imageUrl = "/images/avatar/" + filename;
        user.setIconUrl(imageUrl);

        return UserProfileResponse.from(user);
    }
}


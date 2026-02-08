package com.example.discord.service;

import com.example.discord.dto.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    public ImageResponse upload(MultipartFile image) throws IOException {

        if (!image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능");
        }

        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;

        Path path = Paths.get("uploads/chat").resolve(filename);
        Files.createDirectories(path.getParent());
        Files.copy(image.getInputStream(), path);

        return ImageResponse.builder()
                .imageUrl("/images/chat/" + filename)
                .originalName(image.getOriginalFilename())
                .size(image.getSize())
                .build();
    }
}

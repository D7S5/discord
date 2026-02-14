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

        // image/png
        // image/jpeg
        // image/gi 확인
        if (!image.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능");
        }

        // 확장자 추출
        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;

        Path dir = Paths.get("uploads/chat");
        // uploads/chat 폴더가 없을시 생성
        Files.createDirectories(dir);

        Path path = dir.resolve(filename);
        // 클라이언트 파일을 로컬에 저장
        Files.copy(image.getInputStream(), path);

        return ImageResponse.builder()
                .imageUrl("/images/chat/" + filename)
                .originalName(image.getOriginalFilename())
                .size(image.getSize())
                .build();
    }
}
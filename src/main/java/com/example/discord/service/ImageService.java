package com.example.discord.service;

import com.example.discord.dto.ImageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region}")
    private String region;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/png",
            "image/jpeg",
            "image/jpg",
            "image/gif",
            "image/webp"
    );

    public ImageResponse upload(MultipartFile image) throws IOException {
        validateImage(image);

        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        String key = "chat/" + filename;

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(image.getContentType())
                .build();

        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(image.getBytes())
        );

        String imageUrl = String.format(
                "https://%s.s3.%s.amazonaws.com/%s",
                bucket,
                region,
                key
        );

        return ImageResponse.builder()
                .imageUrl(imageUrl)
                .originalName(image.getOriginalFilename())
                .size(image.getSize())
                .build();
    }

    private void validateImage(MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("빈 파일은 업로드할 수 없습니다.");
        }

        String contentType = image.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("이미지 파일만 업로드 가능합니다.");
        }
    }
}

//    public ImageResponse upload(MultipartFile image) throws IOException {
//
//        // image/png
//        // image/jpeg
//        // image/gi 확인
//        if (!image.getContentType().startsWith("image/")) {
//            throw new IllegalArgumentException("이미지 파일만 업로드 가능");
//        }
//
//        // 확장자 추출
//        String ext = StringUtils.getFilenameExtension(image.getOriginalFilename());
//        String filename = UUID.randomUUID() + "." + ext;
//
//        Path dir = Paths.get("uploads/chat");
//        // uploads/chat 폴더가 없을시 생성
//        Files.createDirectories(dir);
//
//        Path path = dir.resolve(filename);
//        // 클라이언트 파일을 로컬에 저장
//        Files.copy(image.getInputStream(), path);
//
//        return ImageResponse.builder()
//                .imageUrl("/images/chat/" + filename)
//                .originalName(image.getOriginalFilename())
//                .size(image.getSize())
//                .build();
//    }
//}
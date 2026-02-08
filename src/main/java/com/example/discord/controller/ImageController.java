package com.example.discord.controller;

import com.example.discord.dto.ImageResponse;
import com.example.discord.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ImageController {

    private final ImageService imageService;
    @PostMapping("/chat/image")
    public ImageResponse upload(@RequestPart MultipartFile image) throws IOException {
        return imageService.upload(image);
    }
}

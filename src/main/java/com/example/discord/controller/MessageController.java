package com.example.discord.controller;

import com.example.discord.dto.MessageResponse;
import com.example.discord.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/channels")
public class MessageController {

    private final MessageService messageService;

    @GetMapping("/{serverId}{channelId}/messages")
    public List<MessageResponse> history(
            @PathVariable Long channelId
    ) {
        return messageService.getHistory(channelId);
    }
}

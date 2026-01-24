package com.example.discord.controller;

import com.example.discord.dto.DmMessageResponse;
import com.example.discord.service.DmMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages/dm")
@RequiredArgsConstructor
public class DmMessageController {

    private final DmMessageService dmMessageService;

    @GetMapping("/{roomId}")
    public List<DmMessageResponse> getMessages(@PathVariable String roomId) {
        return dmMessageService.getMessages(roomId);
    }
    
}

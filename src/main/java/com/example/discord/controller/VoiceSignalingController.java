//package com.example.discord.controller;
//
//import com.example.discord.dto.voice.VoiceSignalMessage;
//import com.example.discord.security.UserPrincipal;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.security.Principal;
//
//@RestController
//@RequiredArgsConstructor
//@Slf4j
//public class VoiceSignalingController {
//
//    private final SimpMessagingTemplate messagingTemplate;
//
//    @MessageMapping("/voice.signal")
//    public void signal(
//            @Payload VoiceSignalMessage message,
//            @AuthenticationPrincipal UserPrincipal principal
//            ) {
//        String senderId = principal.getId();
//
//        if (!senderId.equals(message.getFrom())) {
//            return ;
//        }
//        messagingTemplate.convertAndSendToUser(
//                message.getTo(),
//                "/queue/voice",
//                message
//        );
//    }
//}

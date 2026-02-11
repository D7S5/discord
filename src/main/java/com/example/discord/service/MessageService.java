package com.example.discord.service;

import com.example.discord.dto.ChatMessageRequest;
import com.example.discord.dto.MessageResponse;
import com.example.discord.entity.Channel;
import com.example.discord.entity.Message;
import com.example.discord.entity.MessageType;
import com.example.discord.entity.User;
import com.example.discord.repository.ChannelRepository;
import com.example.discord.repository.MessageRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Transactional
    public MessageResponse save(Long channelId, String userId, ChatMessageRequest request) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow();

        User user = userRepository.findById(userId)
                .orElseThrow();

        MessageType type = request.getType() != null
                ? request.getType()
                : MessageType.TEXT;

        Message message = Message.builder()
                .channel(channel)
                .sender(user)
                .type(type)
                .content(request.getContent())
                .createdAt(OffsetDateTime.now())
                .build();

        return MessageResponse.from(messageRepository.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getHistory(Long channelId) {
        return messageRepository.findByChannelId(channelId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }
}

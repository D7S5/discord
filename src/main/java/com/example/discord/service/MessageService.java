package com.example.discord.service;

import com.example.discord.dto.MessageResponse;
import com.example.discord.entity.Channel;
import com.example.discord.entity.Message;
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
    public Message save(Long channelId, Long userId, String content) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow();

        User user = userRepository.findById(userId)
                .orElseThrow();

        Message message = Message.builder()
                .channel(channel)
                .sender(user)
                .content(content)
                .createdAt(OffsetDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getHistory(Long channelId) {
        return messageRepository.findByChannelId(channelId)
                .stream()
                .map(MessageResponse::from)
                .toList();
    }
}

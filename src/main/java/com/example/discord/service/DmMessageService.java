package com.example.discord.service;

import com.example.discord.dto.DmMessageResponse;
import com.example.discord.dto.DmRoomDto;
import com.example.discord.dto.MessageResponse;
import com.example.discord.entity.DmMessage;
import com.example.discord.entity.DmRoom;
import com.example.discord.entity.User;
import com.example.discord.repository.DmMessageRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DmMessageService {

    private final DmMessageRepository dmMessageRepository;
    private final UserRepository userRepository;

    public List<DmMessageResponse> getMessages(String roomId) {
        return dmMessageRepository
                .findByRoomIdOrderByCreatedAtAsc(roomId)
                .stream()
                .map(DmMessageResponse::from)
                .toList();
    }

//    public void send(String userId, String roomId) {
//        User sender = userRepository.getReferenceById(userId);
//
//        dmMessageRepository.save(
//                new DmMessage(
//                        new DmRoom(roomId),
//                        sender,
//                        req.getContent()
//                )
//        )
}

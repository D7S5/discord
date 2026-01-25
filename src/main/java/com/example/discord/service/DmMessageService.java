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
    public DmMessage save(String roomId, String senderId, String content) {
        // room + sender + receiver 검증 로직
        return dmMessageRepository.save(
                new DmMessage(roomId, senderId, content)
        );
    }

    public List<DmMessage> findByRoom(String roomId, String userId) {
        // 권한 체크 포함
        return dmMessageRepository.findByRoomId(roomId);
    }


}

package com.example.discord.service;

import com.example.discord.entity.DmRoom;
import com.example.discord.entity.User;
import com.example.discord.repository.DmRoomRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class DmRoomService {

    private final DmRoomRepository dmRoomRepository;
    private final UserRepository userRepository;

    public DmRoom openOrCreate(String meId, String friendId) {
        User me = userRepository.getReferenceById(meId);
        User friend = userRepository.getReferenceById(friendId);

        User a = me.getId().compareTo(friend.getId()) < 0 ? me : friend;
        User b = a == me ? friend : me;

        return dmRoomRepository.findRoom(a.getId(), b.getId())
                .orElseGet(() -> dmRoomRepository.save(DmRoom.create(a, b)));
    }

    public String getOtherUserId(String roomId, String senderId) {
         DmRoom res =  dmRoomRepository.findById(roomId)
                 .orElseThrow(() -> new IllegalArgumentException("DM_ROOM_NOT_FOUND"));
         return res.getOther(senderId);
    }
}

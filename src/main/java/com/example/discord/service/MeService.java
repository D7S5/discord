package com.example.discord.service;

import com.example.discord.dto.DmRoomDto;
import com.example.discord.dto.FriendDto;
import com.example.discord.dto.FriendListDto;
import com.example.discord.dto.MeResponse;
import com.example.discord.repository.DmRoomRepository;
import com.example.discord.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MeService {

    private final FriendRepository friendRepo;
    private final DmRoomRepository dmRepo;

    public MeResponse getMePage(String userId) {

        List<FriendListDto> friends = friendRepo.findFriends(userId)
                .stream()
                .map(f -> new FriendListDto(f.getOther(userId)))
                .toList();

        List<DmRoomDto> dmRooms = dmRepo.findDmRooms(userId)
                .stream()
                .map(d -> new DmRoomDto(
                        d.getId(),
                        d.getOther(userId),
                        d.getLastMessageAt()
                )).toList();

        return new MeResponse(friends, dmRooms);
    }
}
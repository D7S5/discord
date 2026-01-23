package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
@Getter
@AllArgsConstructor
public class MeResponse {
    private List<FriendDto> friends;
    private List<DmRoomDto> dmRooms;
}
package com.example.discord.dto;

import com.example.discord.entity.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class CreateChannelRequest {
    private String name;
    private ChannelType type;
}

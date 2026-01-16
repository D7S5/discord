package com.example.discord.dto;

import com.example.discord.entity.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChannelResponse {

    private Long id;
    private String name;
    private String type;

    public static ChannelResponse from(Channel channel) {
        return new ChannelResponse(
                channel.getId(),
                channel.getName(),
                channel.getType().name()
        );
    }
}

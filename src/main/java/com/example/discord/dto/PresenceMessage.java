package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class PresenceMessage {
    private String userId;
    private String status;
}

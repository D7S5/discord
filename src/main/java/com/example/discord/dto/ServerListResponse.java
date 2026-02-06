package com.example.discord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ServerListResponse {
    private Long id;
    private String name;
//    private String iconUrl;

    private boolean isOwner;   // ⭐ 핵심
    private String myRole;     // OWNER / ADMIN / MEMBER
}

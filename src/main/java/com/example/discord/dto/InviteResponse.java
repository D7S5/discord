package com.example.discord.dto;

import com.example.discord.entity.Invite;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class InviteResponse {

    private String code;
    private OffsetDateTime expiresAt;
    private Integer maxUses;
    private Integer useCount;

    public static InviteResponse from(Invite invite) {
        return new InviteResponse(
                invite.getCode(),
                invite.getExpiresAt(),
                invite.getMaxUses(),
                invite.getUseCount()
        );
    }
}

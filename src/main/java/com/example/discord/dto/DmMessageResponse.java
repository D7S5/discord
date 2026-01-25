package com.example.discord.dto;

import com.example.discord.entity.DmMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class DmMessageResponse {

    private Long id;
    private String roomId;
    private String content;
    private String sender;
    private OffsetDateTime createdAt;

    public static DmMessageResponse from(DmMessage m) {
        return new DmMessageResponse(
                m.getId(),
                m.getRoomId(),
                m.getContent(),
                m.getSenderId(),
                m.getCreatedAt()
        );
    }
}

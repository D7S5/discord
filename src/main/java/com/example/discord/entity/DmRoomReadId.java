package com.example.discord.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DmRoomReadId implements Serializable {

    private String roomId = UUID.randomUUID().toString();
    private String userId = UUID.randomUUID().toString();
}

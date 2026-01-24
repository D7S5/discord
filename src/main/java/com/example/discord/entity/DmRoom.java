package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Entity
@Table(name = "dm_rooms",
 uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userA_id", "userB_id"})
 })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DmRoom extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userA_id")
    private User userA;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userB_id")
    private User userB;

    private OffsetDateTime lastMessageAt;

    public static DmRoom create(User a, User b) {
        DmRoom dmRoom = new DmRoom();
        if(a.getId().compareTo(b.getId()) < 0) {
            dmRoom.userA = a;
            dmRoom.userB = b;
        } else {
            dmRoom.userA = b;
            dmRoom.userB = a;
        }
        return dmRoom;
    }

    public String getOther(String me) {
        return userA.getId().equals(me) ? userB.getId() : userA.getId();
    }
}


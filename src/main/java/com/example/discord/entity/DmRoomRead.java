package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "dm_room_reads",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"room_id", "user_id"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DmRoomRead {

    @EmbeddedId
    private DmRoomReadId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roomId")
    @JoinColumn(name = "room_id")
    private DmRoom room;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    private String lastReadMessageId = UUID.randomUUID().toString();

    public static DmRoomRead create(DmRoom room, User user) {
        DmRoomRead r = new DmRoomRead();
        r.id = new DmRoomReadId(room.getId(), user.getId());
        r.room = room;
        r.user = user;
        return r;
    }

    public void read(String messageId) {
        this.lastReadMessageId = messageId;
    }
}

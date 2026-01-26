package com.example.discord.dto.voice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "voice_rooms")
@Getter
@NoArgsConstructor
public class VoiceRoom {

    @Id
    @Column(length = 36)
    private String id;

    private String name;

    @ElementCollection
    @CollectionTable(name = "voice_room_members", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "user_id")
    private Set<String> members = new HashSet<>();

    public VoiceRoom(String name) {
        this.id = UUID.randomUUID().toString();;
        this.name = name;
    }

    public void join(String userId) {
        members.add(userId);
    }

    public void leave(String userId) {
        members.remove(userId);
    }
}

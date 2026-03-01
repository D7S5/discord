package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    @Id @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    private String iconUrl;
    private String iconKey;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private List<Channel> channels = new ArrayList<>();

    @OneToMany(
            mappedBy = "server",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ServerMember> members = new ArrayList<>();

    public Server(String name, User owner) {
        this.name = name;
        this.owner = owner;
    }

    public boolean isOwner(String userId) {
        return this.owner.getId().equals(userId);
    }
}

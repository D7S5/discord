package com.example.discord.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "blocks",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"blocker_id", "blocked_id"})
}
)
public class Block extends BaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocker_id")
    private User blocker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blocked_id")
    private User blocked;

    public static Block of(User blocker, User blocked) {
        Block b = new Block();
        b.blocker = blocker;
        b.blocked = blocked;
        return b;
    }
}

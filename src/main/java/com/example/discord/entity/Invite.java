package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invites")
@Getter
@NoArgsConstructor
public class Invite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    private OffsetDateTime expiresAt; // null = 무제한
    private Integer maxUses;         // null = 무제한
    private Integer useCount = 0;

    @Version
    private Long version;

    private OffsetDateTime createdAt;


    public Invite(Server server, User creator,
                  OffsetDateTime expiresAt, Integer maxUses, String inviteCode) {
        this.server = server;
        this.creator = creator;
        this.expiresAt = expiresAt;
        this.maxUses = maxUses;
        this.code = inviteCode;
        this.createdAt = OffsetDateTime.now();
    }

    public boolean isExpired() {
        if (expiresAt != null && expiresAt.isBefore(OffsetDateTime.now())) {
            return true;
        }
        if (maxUses != null && useCount >= maxUses) {
            return true;
        }
        return false;
    }

    public void increaseUseCount() {
        this.useCount++;
    }

    public boolean isMaxUsed() {
        return maxUses != null && useCount >= maxUses;
    }
}

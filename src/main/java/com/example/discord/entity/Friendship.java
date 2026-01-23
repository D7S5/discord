package com.example.discord.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "friendships",
        indexes = {
                @Index(name = "idx_friend_user", columnList = "user_id"),
                @Index(name = "idx_friend_target", columnList = "target_id"),
                @Index(name = "idx_friend_status", columnList = "status")},
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "target_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Friendship extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private User target;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendshipStatus status;

    public static Friendship request(User from, User to) {
        Friendship f = new Friendship();
        f.user = from;
        f.target = to;
        f.status = FriendshipStatus.PENDING;
        return f;
    }

    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
    }

    public String getOther(String me) {
        return user.getId().equals(me) ? target.getId() : user.getId();
    }
}

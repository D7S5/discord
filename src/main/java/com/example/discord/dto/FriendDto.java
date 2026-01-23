package com.example.discord.dto;

import com.example.discord.entity.Friendship;
import com.example.discord.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class FriendDto {
    private String friendshipId;
    private String userId;
    private String username;
//    private String avatar;

    public static FriendDto from(Friendship f) {
        User u = f.getUser(); // 요청 보낸 사람
        return new FriendDto(
                f.getId(),
                u.getId(),
                u.getUsername()
//                u.getAvatar()
        );
    }
    public static FriendDto fromAccepted(Friendship f, String myUserId) {
        User other =
                f.getUser().getId().equals(myUserId)
                        ? f.getTarget()
                        : f.getUser();

        return new FriendDto(
                f.getId(),
                other.getId(),
                other.getUsername()
//                other.getAvatar()
        );
    }
}

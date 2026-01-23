package com.example.discord.service;

import com.example.discord.dto.FriendDto;
import com.example.discord.entity.Friendship;
import com.example.discord.entity.FriendshipStatus;
import com.example.discord.entity.User;
import com.example.discord.repository.FriendRepository;
import com.example.discord.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendService {

    private final UserRepository userRepository;
    private final FriendRepository friendRepository;

    public void sendRequest(String fromUserId, String usernameTag) {

        User me = userRepository.findById(fromUserId).orElseThrow();

        User target = userRepository.findByUsername(usernameTag)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (fromUserId.equals(target.getId())) {
            throw new IllegalStateException("Cannot add yourself");
        }

        if (friendRepository.existsBetween(fromUserId, target.getId())) {
            throw new IllegalStateException("Already friends or requested");
        }

        Friendship friendship = Friendship.request(me, target);
        friendRepository.save(friendship);
    }

    @Transactional
    public void acceptRequest(String myUserId, String friendshipId) {

        Friendship friendship = friendRepository.findById(friendshipId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!friendship.getTarget().getId().equals(myUserId)) {
            throw new IllegalStateException("No permission to accept");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalStateException("Already processed request");
        }

        friendship.accept();
    }

    public List<FriendDto> getReceivedRequests(String userId) {
        return friendRepository.findReceivedRequests(userId)
                .stream()
                .map(FriendDto::from)
                .toList();
    }

    public List<FriendDto> getFriends(String userId) {
        return friendRepository.findFriends(userId)
                .stream()
                .map(f -> FriendDto.fromAccepted(f, userId))
                .toList();
    }
}
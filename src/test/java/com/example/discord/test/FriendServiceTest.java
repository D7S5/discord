package com.example.discord.test;

import com.example.discord.entity.Friendship;
import com.example.discord.entity.FriendshipStatus;
import com.example.discord.entity.User;
import com.example.discord.repository.FriendRepository;
import com.example.discord.repository.UserRepository;
import com.example.discord.service.FriendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @Mock UserRepository userRepository;
    @Mock FriendRepository friendRepository;

    FriendService friendService;

    @BeforeEach
    void setUp() {
        friendService = new FriendService(userRepository, friendRepository);
    }

    @Test
    void sendRequest_success_savesPendingFriendship() {
        // given
        String fromUserId = "A";
        String targetUsername = "Bname";

        User me = mockUser("A");
        User target = mockUser("B");

        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(me));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));
        when(friendRepository.existsBetween(anyString(), anyString())).thenReturn(false);

        // when
        friendService.sendRequest(fromUserId, targetUsername);

        // then
        ArgumentCaptor<Friendship> captor = ArgumentCaptor.forClass(Friendship.class);
        verify(friendRepository).save(captor.capture());
        verify(friendRepository).existsBetween(anyString(), anyString());

        Friendship saved = captor.getValue();
        assertThat(saved.getUser()).isSameAs(me);
        assertThat(saved.getTarget()).isSameAs(target);
        assertThat(saved.getStatus()).isEqualTo(FriendshipStatus.PENDING);
//        save가 정확히 1번 호출됐는지 확인.
        verify(friendRepository, times(1)).save(any(Friendship.class));

    }

    @Test
    void sendRequest_fail_when_addSelf() {
        // given
        String fromUserId = "A";
        String targetUsername = "Aname";

        User me = mockUser("A");
        User target = me;

        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(me));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));

        // when & then
        assertThatThrownBy(() -> friendService.sendRequest(fromUserId, targetUsername))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot add yourself");

        // save 호출 0회 never
        verify(friendRepository, never()).save(any());
    }

//     이미 친구/요청 관계가 있으면 예외가 나야 한다.
    @Test
    void sendRequest_fail_when_already_exists_between() {
        // given
        String fromUserId = "A";
        String targetUsername = "Bname";

        User me = mockUser("A");
        User target = mockUser("B");

        when(userRepository.findById(fromUserId)).thenReturn(Optional.of(me));
        when(userRepository.findByUsername(targetUsername)).thenReturn(Optional.of(target));
        when(friendRepository.existsBetween(anyString(), anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> friendService.sendRequest(fromUserId, targetUsername))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Already friends or requested");

        verify(friendRepository, never()).save(any());
    }

    private User mockUser(String id) {
        User u = mock(User.class);
        lenient().when(u.getId()).thenReturn(id);
        return u;
    }
}

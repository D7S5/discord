package com.example.discord.test;

import com.example.discord.entity.Friendship;
import com.example.discord.entity.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

class FriendshipTest {

    @Test
    void accept_changes_status_to_ACCEPTED() {
        User a = mockUser("A");
        User b = mockUser("B");

        Friendship f = Friendship.request(a, b);
        f.accept();

        assertThat(f.getStatus().name()).isEqualTo("ACCEPTED");
    }

    private User mockUser(String id) {
        User u = mock(User.class);
        lenient().when(u.getId()).thenReturn(id);
        return u;
    }
}

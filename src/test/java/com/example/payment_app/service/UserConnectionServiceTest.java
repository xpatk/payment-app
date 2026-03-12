package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.model.UserConnection;
import com.example.payment_app.repository.UserConnectionRepository;
import com.example.payment_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserConnectionServiceTest {

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserConnectionService userConnectionService;

    /**
     * Should return list of connected users for given user.
     */
    @Test
    void shouldReturnConnectionsForUser() {

        User user = new User();
        user.setUserId(1);

        User friend = new User();
        friend.setUserId(2);

        UserConnection connection = new UserConnection();
        connection.setUser(user);
        connection.setConnection(friend);

        when(userConnectionRepository.findByUser(user))
                .thenReturn(List.of(connection));

        List<User> result = userConnectionService.getConnectionsForUser(user);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(friend);
    }

    /**
     * Should return an empty line
     */

    @Test
    void shouldReturnEmptyListWhenUserHasNoConnections() {

        User user = new User();
        user.setUserId(1);

        when(userConnectionRepository.findByUser(user))
                .thenReturn(List.of());

        List<User> result = userConnectionService.getConnectionsForUser(user);

        assertThat(result).isEmpty();
    }

    /**
     * Should create a new connection when valid user email is provided.
     */
    @Test
    void shouldSaveConnectionSuccessfully() {

        User user = new User();
        user.setUserId(1);

        User friend = new User();
        friend.setUserId(2);

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(friend);

        when(userConnectionRepository.existsByUserAndConnection(user, friend))
                .thenReturn(false);

        User result = userConnectionService.saveConnection(user, "friend@mail.com");

        verify(userConnectionRepository).save(any(UserConnection.class));
        assertThat(result).isEqualTo(friend);
    }

    /**
     * Should throw exception when connection email does not exist.
     */
    @Test
    void shouldThrowWhenUserNotFound() {

        User user = new User();
        user.setUserId(1);

        when(userRepository.findByEmail("unknown@mail.com"))
                .thenReturn(null);

        assertThatThrownBy(() ->
                userConnectionService.saveConnection(user, "unknown@mail.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    /**
     * Should throw exception when user tries to add themselves as connection.
     */
    @Test
    void shouldThrowWhenAddingSelf() {

        User user = new User();
        user.setUserId(1);

        when(userRepository.findByEmail("self@mail.com"))
                .thenReturn(user);

        assertThatThrownBy(() ->
                userConnectionService.saveConnection(user, "self@mail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("own account");
    }

    /**
     * Should throw exception when connection already exists.
     */
    @Test
    void shouldThrowWhenConnectionAlreadyExists() {

        User user = new User();
        user.setUserId(1);

        User friend = new User();
        friend.setUserId(2);

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(friend);

        when(userConnectionRepository.existsByUserAndConnection(user, friend))
                .thenReturn(true);

        assertThatThrownBy(() ->
                userConnectionService.saveConnection(user, "friend@mail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }
    /**
     * Should delete connection when it exists.
     */
    @Test
    void shouldDeleteConnectionSuccessfully() {

        User user = new User();
        user.setUserId(1);

        User friend = new User();
        friend.setUserId(2);

        UserConnection connection = new UserConnection();
        connection.setUser(user);
        connection.setConnection(friend);

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(friend);

        when(userConnectionRepository.findByUser(user))
                .thenReturn(List.of(connection));

        userConnectionService.deleteConnection(user, "friend@mail.com");

        verify(userConnectionRepository).delete(connection);
    }

    /**
     * Should throw exception when connection does not exist.
     */
    @Test
    void shouldThrowWhenDeletingNonExistingConnection() {

        User user = new User();
        user.setUserId(1);

        User friend = new User();
        friend.setUserId(2);

        when(userRepository.findByEmail("friend@mail.com"))
                .thenReturn(friend);

        when(userConnectionRepository.findByUser(user))
                .thenReturn(List.of());

        assertThatThrownBy(() ->
                userConnectionService.deleteConnection(user, "friend@mail.com"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Connection not found");
    }
}

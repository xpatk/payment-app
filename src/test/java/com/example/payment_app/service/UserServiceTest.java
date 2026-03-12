package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private UserService userService;


    /*
    REGISTER USER TESTS
     */

    /**
     * Should register a new user when provided with valid data.
     */
    @Test
    void registerUserWhenValidData() {

        // GIVEN
        when(userRepository.findByEmail("test@mail.com")).thenReturn(null);
        when(userRepository.findByUsername("testUser")).thenReturn(null);
        when(bCryptPasswordEncoder.encode("password")).thenReturn("encodedPassword");

        // WHEN
        userService.registerUser("testUser", "test@mail.com", "password");

        // THEN
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertThat(savedUser.getUsername()).isEqualTo("testUser");
        assertThat(savedUser.getEmail()).isEqualTo("test@mail.com");
        assertThat(savedUser.getPassword()).isEqualTo("encodedPassword");
    }


    /**
     * Should throw exception when username is blank during registration.
     */
    @Test
    void shouldThrowExceptionWhenUsernameIsBlank() {

        assertThatThrownBy(() ->
                userService.registerUser("", "mail", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }


    /**
     * Should throw exception when email is blank during registration.
     */
    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {

        assertThatThrownBy(() ->
                userService.registerUser("user", "", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }


    /**
     * Should throw exception when password is blank during registration.
     */
    @Test
    void shouldThrowExceptionWhenPasswordIsBlank() {

        assertThatThrownBy(() ->
                userService.registerUser("user", "mail", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }


    /**
     * Should throw exception when email already exists in the system.
     */
    @Test
    void shouldThrowExceptionWhenEmailExists() {

        when(userRepository.findByEmail("test@mail.com"))
                .thenReturn(new User());

        assertThatThrownBy(() ->
                userService.registerUser("user", "test@mail.com", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }


    /**
     * Should throw exception when username already exists in the system.
     */
    @Test
    void shouldThrowExceptionWhenUsernameExists() {

        when(userRepository.findByEmail("test@mail.com")).thenReturn(null);
        when(userRepository.findByUsername("user")).thenReturn(new User());

        assertThatThrownBy(() ->
                userService.registerUser("user", "test@mail.com", "pass"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }


    /*
    UPDATE USER TESTS
     */

    /**
     * Should throw exception when user does not exist during update.
     */
    @Test
    void shouldThrowWhenUserNotFound() {

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                userService.updateUser(1, "newUser", "mail"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found");
    }


    /**
     * Should throw exception when username is blank during update.
     */
    @Test
    void shouldThrowWhenUsernameIsBlankOnUpdate() {

        User existing = new User();
        existing.setUserId(1);
        existing.setEmail("old@mail.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                userService.updateUser(1, "", "mail"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }


    /**
     * Should throw exception when email is blank during update.
     */
    @Test
    void shouldThrowWhenEmailIsBlankOnUpdate() {

        User existing = new User();
        existing.setUserId(1);
        existing.setUsername("username");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() ->
                userService.updateUser(1, "username", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }


    /**
     * Should throw exception when email is already used by another user.
     */
    @Test
    void shouldThrowWhenEmailAlreadyUsedByAnotherUser() {

        User existing = new User();
        existing.setUserId(1);
        existing.setEmail("old@mail.com");

        User otherUser = new User();
        otherUser.setUserId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(otherUser);

        assertThatThrownBy(() ->
                userService.updateUser(1, "newUser", "new@mail.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }


    /**
     * Should throw exception when username is already used by another user.
     */
    @Test
    void shouldThrowWhenUsernameAlreadyUsed() {

        User existing = new User();
        existing.setUserId(1);
        existing.setEmail("old@mail.com");

        User otherUser = new User();
        otherUser.setUserId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("mail")).thenReturn(null);
        when(userRepository.findByUsername("newUser")).thenReturn(otherUser);

        assertThatThrownBy(() ->
                userService.updateUser(1, "newUser", "mail"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Username");
    }


    /**
     * Should refresh authentication when email changes.
     */
    @Test
    void shouldRefreshAuthenticationWhenEmailChanges() {

        User existing = new User();
        existing.setUserId(1);
        existing.setUsername("oldUser");
        existing.setEmail("old@mail.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("new@mail.com")).thenReturn(null);
        when(userRepository.findByUsername("newUser")).thenReturn(null);

        userService.updateUser(1, "newUser", "new@mail.com");

        verify(securityService).refreshAuthentication(existing);
    }


    /**
     * Should not refresh authentication when email does not change.
     */
    @Test
    void shouldNotRefreshAuthenticationWhenEmailNotChanged() {

        User existing = new User();
        existing.setUserId(1);
        existing.setUsername("oldUser");
        existing.setEmail("same@mail.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(userRepository.findByEmail("same@mail.com")).thenReturn(existing);
        when(userRepository.findByUsername("newUser")).thenReturn(null);

        userService.updateUser(1, "newUser", "same@mail.com");

        verify(securityService, never()).refreshAuthentication(any());
    }


    /*
    UPDATE PASSWORD TESTS
     */

    /**
     * Should throw exception when password is blank during password update.
     */
    @Test
    void shouldThrowWhenPasswordIsBlank() {

        assertThatThrownBy(() ->
                userService.updatePassword(1, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Password");
    }


    /**
     * Should encode password when updating password.
     */
    @Test
    void shouldEncodePasswordWhenUpdatingPassword() {

        User existing = new User();
        existing.setUserId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(existing));
        when(bCryptPasswordEncoder.encode("newPass")).thenReturn("encodedPass");

        userService.updatePassword(1, "newPass");

        assertThat(existing.getPassword()).isEqualTo("encodedPass");
    }
}
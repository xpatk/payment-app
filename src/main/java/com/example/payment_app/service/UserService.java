package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for user management logic such as
 * registration, profile updates and password changes.
 */

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * Finds a user by email.
     *
     * @param email the user's email
     * @return the User entity or null if not found
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Registers a new user after validating uniqueness of email and username.
     *
     * @param username the chosen username
     * @param email    the user's email
     * @param password the raw password (will be encrypted)
     * @return the persisted User entity
     * @throws IllegalArgumentException if email or username already exists
     */
    @Transactional
    public User registerUser(String username, String email, String password) {

        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (userRepository.findByEmail(email) != null) {
            throw new IllegalArgumentException("An account already exists for this email");
        }
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("Username already taken");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));

        return userRepository.save(user);
    }

    /**
     * Updates username and email of an existing user.
     *
     * @param userId      ID of the user to update
     * @param newUsername new username
     * @param newEmail    new email
     * @return updated User entity
     */
    @Transactional
    public User updateUser(Integer userId, String newUsername, String newEmail) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (newUsername == null || newUsername.isBlank()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (newEmail == null || newEmail.isBlank()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        User existingByEmail = userRepository.findByEmail(newEmail);
        if (existingByEmail != null && !existingByEmail.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Email already in use");
        }
        User existingByUsername = userRepository.findByUsername(newUsername);
        if (existingByUsername != null && !existingByUsername.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Username already in use");
        }
        user.setUsername(newUsername);
        user.setEmail(newEmail);

        return user;
    }

    /**
     * Updates the password of a user.
     * @param userId      ID of the user
     * @param newPassword raw password (will be encrypted)
     */
    @Transactional
    public void updatePassword(Integer userId, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
    }
}

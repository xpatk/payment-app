package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User registerUser(String username, String email, String password) {

        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("An account exists for this email already");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        return userRepository.save(user);
    }

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

        User existing = userRepository.findByEmail(newEmail);
        if (existing != null && !existing.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setUsername(newUsername);
        user.setEmail(newEmail);

        return user;
    }

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

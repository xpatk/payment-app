package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);

    }

    @Transactional
    public User updateUser(User user, String newUsername, String newEmail){
        user.setUsername(newUsername);
        user.setEmail(newEmail);

        return user;
    }

    @Transactional
    public User updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));

        return user;
    }
}

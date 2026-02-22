package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.model.UserConnection;
import com.example.payment_app.repository.UserConnectionRepository;
import com.example.payment_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConnectionService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Autowired
    private UserRepository userRepository;

    public List<User> getConnectionsForUser(User user) {
        return userConnectionRepository.findByUser(user)
                .stream()
                .map(UserConnection::getConnection)
                .toList();
    }

    @Transactional
    public User saveConnection(User user, String connectionEmail) {
        User connection = userRepository.findByEmail(connectionEmail);
        if(connection == null) {
            throw new RuntimeException("User not found");
        }
        if(user.getUserId().equals(connection.getUserId())) {
            throw new IllegalArgumentException("You cannot add your own account as connection");
        }
        boolean alreadyExists = userConnectionRepository
                .findByUser(user)
                .stream()
                .anyMatch(uc -> uc.getConnection().getUserId().equals(connection.getUserId()));
        if (alreadyExists) {
            throw new RuntimeException("This connection already exists.");
        }
        UserConnection userConnection = new UserConnection();
        userConnection.setUser(user);
        userConnection.setConnection(connection);
        userConnectionRepository.save(userConnection);
        return connection;
    }

    @Transactional
    public void deleteConnection (User user, String connectionEmail) {

        User connection = userRepository.findByEmail(connectionEmail);
        if (connection == null) {
            throw new RuntimeException("This connection doesn't exist");
        }
        UserConnection userConnection = userConnectionRepository
                .findByUser(user)
                .stream()
                .filter(uc -> uc.getConnection().getUserId().equals(connection.getUserId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Connection not found."));
        userConnectionRepository.delete(userConnection);
    }

}
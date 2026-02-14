package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.model.UserConnection;
import com.example.payment_app.repository.UserConnectionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserConnectionService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    public List<User> getConnectionsForUser(User user) {
        return userConnectionRepository.findByUser(user)
                .stream()
                .map(UserConnection::getConnection)
                .toList();
    }

    @Transactional
    public User saveConnection(User user, String connectionEmail) {
        //check if connection exists
        //add connection
    }

    // delete connection

}
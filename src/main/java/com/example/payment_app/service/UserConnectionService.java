package com.example.payment_app.service;

import com.example.payment_app.model.User;
import com.example.payment_app.model.UserConnection;
import com.example.payment_app.repository.UserConnectionRepository;
import com.example.payment_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for managing user relationships (connections).
 *
 * Handles:
 * - retrieving user connections
 * - adding new connections
 * - deleting existing connections
 *
 * This layer contains business validation logic such as:
 * - preventing users from adding themselves
 * - preventing duplicate connections
 */
@Service
public class UserConnectionService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Returns a list of users connected to the given user.
     *
     * @param user the authenticated user
     * @return list of connected users
     */
    public List<User> getConnectionsForUser(User user) {
        return userConnectionRepository.findByUser(user)
                .stream()
                .map(UserConnection::getConnection)
                .toList();
    }

    /**
     * Creates a new connection between the given user and another user identified by email.
     *
     * - The connection email must exist in the database
     * - A user cannot add themselves
     * - Duplicate connections are not allowed
     *
     * @param user the authenticated user
     * @param connectionEmail email of the user to connect with
     * @return the connected User entity
     * @throws RuntimeException if the target user does not exist or connection already exists
     * @throws IllegalArgumentException if user attempts to add themselves
     */
    @Transactional
    public User saveConnection(User user, String connectionEmail) {

        User connection = userRepository.findByEmail(connectionEmail);

        if (connection == null) {
            throw new RuntimeException("User not found");
        }

        if (user.getUserId().equals(connection.getUserId())) {
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

    /**
     * Deletes an existing connection between the given user and another user.
     *
     * @param user the authenticated user
     * @param connectionEmail email of the connected user to remove
     * @throws RuntimeException if connection does not exist
     */
    @Transactional
    public void deleteConnection(User user, String connectionEmail) {

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
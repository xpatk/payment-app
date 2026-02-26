package com.example.payment_app.repository;

import com.example.payment_app.model.User;
import com.example.payment_app.model.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing UserConnection entities.
 *
 * Provides access to relationship data between users.
 * Extends JpaRepository to inherit standard CRUD operations.
 */
@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Integer> {

    /**
     * Retrieves all connections for a given user.
     *
     * Each UserConnection represents a relationship between
     * the owning user and another connected user.
     *
     * @param user the owner of the connections
     * @return list of UserConnection entities associated with the user
     */
    List<UserConnection> findByUser(User user);

    /**
     * Checks whether a connection already exists between two users.
     *
     * @param user owner of the connection
     * @param connection connected user
     * @return true if connection exists
     */
    boolean existsByUserAndConnection(User user, User connection);

}
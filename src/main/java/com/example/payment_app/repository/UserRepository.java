package com.example.payment_app.repository;

import com.example.payment_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for User entity.
 *
 * Provides CRUD operations and custom query methods
 * for accessing user data in the database.
 *
 * Extends JpaRepository which automatically supplies other common persistence methods.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Retrieves a user by email address.
     *
     * Used primarily for authentication and registration validation.
     *
     * @param email unique email of the user
     * @return User entity if found, otherwise null
     */
    User findByEmail(String email);

    /**
     * Retrieves a user by username.
     *
     * Useful for validating username uniqueness during registration
     * or profile updates.
     *
     * @param username unique username of the user
     * @return User entity if found, otherwise null
     */
    User findByUsername(String username);
}
package com.example.payment_app.repository;

import com.example.payment_app.model.Transaction;
import com.example.payment_app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides database access for financial transactions between users.
 * Inherits standard CRUD operations from JpaRepository.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    /**
     * Retrieves all transactions where the given user is the sender.
     *
     * @param sender the user who initiated the transaction
     * @return list of transactions sent by the user
     */
    List<Transaction> findBySender(User sender);

    /**
     * Retrieves all transactions where the given user is the receiver.
     *
     * @param receiver the user who received the transaction
     * @return list of transactions received by the user
     */
    List<Transaction> findByReceiver(User receiver);

    /**
     * Retrieves all transactions where the given user
     * is either the sender or the receiver.
     *
     * Commonly used to display a complete transaction history
     * for a specific user.
     *
     * @param sender the user as transaction sender
     * @param receiver the user as transaction receiver
     * @return list of transactions involving the user
     */
    List<Transaction> findBySenderOrReceiver(User sender, User receiver);
}
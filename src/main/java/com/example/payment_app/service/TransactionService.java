package com.example.payment_app.service;

import com.example.payment_app.model.Transaction;
import com.example.payment_app.model.User;
import com.example.payment_app.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for money transfer logic between users.
 */
@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * Returns all transactions where the user is sender or receiver.
     *
     * @param user the user
     * @return list of transactions
     */
    public List<Transaction> getAllUserTransactions(User user) {
        return transactionRepository.findBySenderOrReceiver(user, user);
    }

    /**
     * Sends money from one user to another.
     *
     * @param sender   the user initiating the transfer
     * @param receiver the receiving user
     * @param amount   amount to transfer (must be positive)
     * @return persisted Transaction
     * @throws IllegalArgumentException if validation fails
     */
    @Transactional
    public Transaction sendMoney(User sender, User receiver, Double amount) {

        if (sender == null) {
            throw new IllegalArgumentException("Sender cannot be null");
        }
        if (receiver == null) {
            throw new IllegalArgumentException("Receiver cannot be null");
        }
        if (sender.getUserId().equals(receiver.getUserId())) {
            throw new IllegalArgumentException("You cannot send money to yourself");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);

        return transactionRepository.save(transaction);
    }
}
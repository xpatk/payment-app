package com.example.payment_app.service;

import com.example.payment_app.model.Transaction;
import com.example.payment_app.model.User;
import com.example.payment_app.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> getAllUserTransactions(User user) {
        return transactionRepository.findBySenderOrReceiver(user, user);
    }

    @Transactional
    public Transaction sendMoney(User sender, User receiver, Double amount) {
        Transaction transaction = new Transaction();
        transaction.setSender(sender);
        transaction.setReceiver(receiver);
        transaction.setAmount(amount);

        return transactionRepository.save(transaction);
    }
}

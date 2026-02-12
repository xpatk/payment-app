package com.example.payment_app.service;

import com.example.payment_app.model.Transaction;
import com.example.payment_app.model.User;
import com.example.payment_app.repository.TransactionRepository;
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
}

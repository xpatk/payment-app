package com.example.payment_app.service;

import com.example.payment_app.model.UserConnection;
import com.example.payment_app.repository.UserConnectionRepository;
import com.example.payment_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConnectionService {

    @Autowired
    private UserConnectionRepository userConnectionRepository;
}
package com.example.payment_app.repository;

import com.example.payment_app.model.User;
import com.example.payment_app.model.UserConnection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserConnectionRepository extends JpaRepository<UserConnection, Integer> {

    List<UserConnection> findByUser(User user);

}

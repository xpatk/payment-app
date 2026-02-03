package com.example.payment_app.repository;

import com.example.payment_app.model.UserConnection;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserConnectionRepository extends CrudRepository<UserConnection, Integer> {
}

package com.example.payment_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name="transactions")
public class Transaction {

    private Integer id;
    private Integer sender_id;
    private Integer receiver_id;
    private String description;
    private Double amount;

}

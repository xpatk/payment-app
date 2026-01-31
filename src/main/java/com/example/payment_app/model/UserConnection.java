package com.example.payment_app.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_connections")
public class UserConnection {

    @EmbeddedId
    private UserConnectionId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("connectionId")
    @JoinColumn(name = "connection_id", nullable = false)
    private User connection;
}
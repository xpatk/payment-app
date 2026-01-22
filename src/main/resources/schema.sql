-- CREATE A NEW DATABASE

DROP DATABASE IF EXISTS paymybuddy;
CREATE DATABASE paymybuddy;
USE paymybuddy;

-- TABLE USERS

CREATE TABLE USERS (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- TABLE: TRANSACTIONS

CREATE TABLE TRANSACTIONS (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sender INT NOT NULL,
    receiver INT NOT NULL,
    description VARCHAR(255),
    amount DOUBLE NOT NULL,
    CONSTRAINT fk_transactions_sender
        FOREIGN KEY (sender) REFERENCES USERS(id),
    CONSTRAINT fk_transactions_receiver
        FOREIGN KEY (receiver) REFERENCES USERS(id)
);

-- TABLE: USER_CONNECTIONS

CREATE TABLE USER_CONNECTIONS (
    user_id INT NOT NULL,
    connection_id INT NOT NULL,
    PRIMARY KEY (user_id, connection_id),
    CONSTRAINT fk_user
        FOREIGN KEY (user_id) REFERENCES USERS(id) ON DELETE CASCADE,
    CONSTRAINT fk_connection
        FOREIGN KEY (connection_id) REFERENCES USERS(id) ON DELETE CASCADE
);


--  TEST DATA - USERS

INSERT INTO USERS (username, email, password) VALUES
('alice', 'alice@test.com', '$2a$10$hashAlice'),
('bob', 'bob@test.com', '$2a$10$hashBob'),
('charlie', 'charlie@test.com', '$2a$10$hashCharlie'),
('diana', 'diana@test.com', '$2a$10$hashDiana');

-- TEST DATA - USER_CONNECTIONS

INSERT INTO USER_CONNECTIONS (user_id, connection_id) VALUES
(1, 2),
(1, 3),
(2, 1),
(2, 4),
(3, 1),
(3, 4),
(4, 2);

-- TEST DATA - TRANSACTIONS

INSERT INTO TRANSACTIONS (sender, receiver, description, amount) VALUES
(1, 2, 'Lunch', 25.50),
(2, 1, 'Refund', 10.00),
(3, 4, 'Gift', 40.00),
(4, 2, 'Concert tickets', 75.00);
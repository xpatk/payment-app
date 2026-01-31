-- CLEANUP

DROP TABLE IF EXISTS user_connections;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS users;

-- TABLE USERS

CREATE TABLE users (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

-- TABLE: TRANSACTIONS

CREATE TABLE transactions (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    sender_id INT NOT NULL,
    receiver_id INT NOT NULL,
    description VARCHAR(255),
    amount DOUBLE NOT NULL,

    CONSTRAINT fk_transactions_sender
        FOREIGN KEY (sender_id) REFERENCES users(id),

    CONSTRAINT fk_transactions_receiver
        FOREIGN KEY (receiver_id) REFERENCES users(id)
);

-- TABLE: USER_CONNECTIONS

CREATE TABLE user_connections (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    connection_id INT NOT NULL,

    UNIQUE KEY uk_user_connection (user_id, connection_id)

    CONSTRAINT fk_user_connections_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT fk_user_connections_connection
        FOREIGN KEY (connection_id) REFERENCES users(id) ON DELETE CASCADE
);


--  TEST DATA - USERS
-- passwords are placeholders

INSERT INTO users (username, email, password) VALUES
('alice', 'alice@test.com', '$2a$10$hashAlice'),
('bob', 'bob@test.com', '$2a$10$hashBob'),
('charlie', 'charlie@test.com', '$2a$10$hashCharlie'),
('diana', 'diana@test.com', '$2a$10$hashDiana');

-- TEST DATA - USER_CONNECTIONS

INSERT INTO user_connections (user_id, connection_id) VALUES
(1, 2),
(1, 3),
(2, 1),
(2, 4),
(3, 1),
(3, 4),
(4, 2);

-- TEST DATA - TRANSACTIONS

INSERT INTO transactions (sender_id, receiver_id, description, amount) VALUES
(1, 2, 'Lunch', 25.50),
(2, 1, 'Refund', 10.00),
(3, 4, 'Gift', 40.00),
(4, 2, 'Concert tickets', 75.00);
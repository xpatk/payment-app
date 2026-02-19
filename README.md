# PayMyBuddy

🚧 **PROJECT STATUS: UNDER DEVELOPMENT**

---

## General Application Purpose

PayMyBuddy is a Spring Boot application that allows users to transfer money between each other in order to manage payments and personal financial connections.

This project is developed in a learning context and focuses on backend architecture, database design, transaction management, and data persistence using Spring Data JPA.

---

## Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- Spring Security
- Thymeleaf
- MySQL
- Maven
- JUnit

---

## Database Setup

The application uses a MySQL database.

The database must exist before running the application.

Create it manually in MySQL:

```sql
CREATE DATABASE paymentapp;
```

---

## Database Initialization



The database structure is initialized automatically at application startup using an SQL script.

This script:

- Creates the following tables:
    - `users`
    - `transactions`
    - `user_connections`
- Defines primary keys and foreign keys
- Inserts default test data into the `users`, `transactions`, and `user_connections` tables

---

## Database Schema

![Database schema](bd-schema.png)

---

## Configuration (IMPORTANT)

Database credentials are not hardcoded in the project.

The application uses environment variables for database authentication.

### application.properties

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/paymentapp?serverTimezone=UTC
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always
```

---

## Required Environment Variables

Before running the application, you must define the following environment variables:

- `DB_USER`
- `DB_PASSWORD`

These variables can be configured:

- In IntelliJ → Run Configuration → Environment Variables
- Or as system environment variables

---

## How to Run the Application

1. Make sure MySQL is running.
2. Make sure the database `paymentapp` exists.
3. Configure the required environment variables.
4. Run the application:

```bash
mvn spring-boot:run
```

The application will start on:

```
http://localhost:8080
```

---

## Project Structure

The application follows a layered architecture:

- **Model** → JPA Entities
- **Repository** → Spring Data JPA interfaces
- **Service** → Business logic
- **Configuration** → Security and application configuration

---

## Testing

Unit and integration tests will validate:

- Database connectivity
- Repository behavior
- Service layer logic
- Transaction rollback behavior

---

## Notes

- The database must be running before application startup.
- SQL schema is managed manually (Hibernate auto-ddl is disabled).
- Passwords are encrypted using BCrypt.

---

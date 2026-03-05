package com.example.payment_app.service;

import com.example.payment_app.model.Transaction;
import com.example.payment_app.model.User;
import com.example.payment_app.repository.TransactionRepository;
import com.example.payment_app.repository.UserConnectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.ArgumentCaptor;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserConnectionRepository userConnectionRepository;

    @InjectMocks
    private TransactionService transactionService;


    /**
     * Should create and persist a transaction when all conditions are valid.
     */
    @Test
    void shouldSendMoneySuccessfully() {

        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        when(userConnectionRepository.existsByUserAndConnection(sender, receiver))
                .thenReturn(true);

        Transaction savedTransaction = new Transaction();
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(savedTransaction);

        Transaction result = transactionService.sendMoney(sender, receiver, 50.0, "Ticket");

        verify(transactionRepository).save(any(Transaction.class));
        assertThat(result).isNotNull();
    }

    /**
     * Should throw exception when sender is null.
     */
    @Test
    void shouldThrowWhenSenderIsNull() {

        User receiver = new User();
        receiver.setUserId(1);

        assertThatThrownBy(() ->
                transactionService.sendMoney(null, receiver, 50.0, "Ticket"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sender");
    }

    /**
     * Should throw exception when receiver is null.
     */
    @Test
    void shouldThrowWhenReceiverIsNull() {

        User sender = new User();
        sender.setUserId(1);

        assertThatThrownBy(() ->
                transactionService.sendMoney(sender, null, 50.0, "Dinner"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Receiver");
    }

    /**
     * Should throw exception when sender tries to send money to themselves.
     */
    @Test
    void shouldThrowWhenSenderEqualsReceiver() {

        User user = new User();
        user.setUserId(1);

        assertThatThrownBy(() ->
                transactionService.sendMoney(user, user, 50.0, "Test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("yourself");
    }

    /**
     * Should throw exception when amount is invalid.
     */
    @Test
    void shouldThrowWhenAmountIsInvalid() {

        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        assertThatThrownBy(() ->
                transactionService.sendMoney(sender, receiver, 0.0, "Test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Amount");
    }

    /**
     * Should throw exception when users are not connected.
     */
    @Test
    void shouldThrowWhenUsersAreNotConnected() {

        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        when(userConnectionRepository.existsByUserAndConnection(sender, receiver))
                .thenReturn(false);

        assertThatThrownBy(() ->
                transactionService.sendMoney(sender, receiver, 50.0, "Dinner"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("connections");
    }

    /**
     * Should return all transactions where user is sender or receiver.
     */
    @Test
    void shouldReturnUserTransactions() {

        User user = new User();

        List<Transaction> transactions =
                List.of(new Transaction(), new Transaction());

        when(transactionRepository.findBySenderOrReceiver(user, user))
                .thenReturn(transactions);

        List<Transaction> result =
                transactionService.getAllUserTransactions(user);

        assertThat(result).hasSize(2);
    }

    /**
     * Should build and persist a correct transaction when sending money.
     */
    @Test
    void shouldCreateCorrectTransactionObject() {

        // GIVEN
        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        when(userConnectionRepository.existsByUserAndConnection(sender, receiver))
                .thenReturn(true);

        Transaction saved = new Transaction();
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(saved);

        // WHEN
        transactionService.sendMoney(sender, receiver, 100.0, "Dinner");

        // THEN
        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction captured = transactionCaptor.getValue();

        assertThat(captured.getSender()).isEqualTo(sender);
        assertThat(captured.getReceiver()).isEqualTo(receiver);
        assertThat(captured.getAmount()).isEqualTo(100.0);
        assertThat(captured.getDescription()).isEqualTo("Dinner");
    }
}

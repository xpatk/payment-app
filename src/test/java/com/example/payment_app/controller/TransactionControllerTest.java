package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.TransactionService;
import com.example.payment_app.service.UserConnectionService;
import com.example.payment_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserConnectionService userConnectionService;

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnTransactionsPage() throws Exception {

        User user = new User();
        user.setUserId(1);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        when(transactionService.getAllUserTransactions(user))
                .thenReturn(List.of());

        when(userConnectionService.getConnectionsForUser(user))
                .thenReturn(List.of());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"))
                .andExpect(model().attributeExists("transactions"))
                .andExpect(model().attributeExists("connections"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldSendMoneySuccessfully() throws Exception {

        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(sender);

        when(userService.findByEmail("friend@mail.com"))
                .thenReturn(receiver);

        mockMvc.perform(post("/transactions")
                        .param("receiverEmail", "friend@mail.com")
                        .param("description", "Dinner")
                        .param("amount", "50"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));

        verify(transactionService)
                .sendMoney(sender, receiver, 50.0, "Dinner");
    }


    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnTransactionsWhenTransferFails() throws Exception {

        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(sender);

        when(userService.findByEmail("friend@mail.com"))
                .thenReturn(receiver);

        doThrow(new IllegalArgumentException("Invalid"))
                .when(transactionService)
                .sendMoney(sender, receiver, 50.0, "Dinner");

        when(transactionService.getAllUserTransactions(sender))
                .thenReturn(List.of());

        when(userConnectionService.getConnectionsForUser(sender))
                .thenReturn(List.of());

        mockMvc.perform(post("/transactions")
                        .param("receiverEmail", "friend@mail.com")
                        .param("description", "Dinner")
                        .param("amount", "50"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"))
                .andExpect(model().attributeExists("error"));
    }
}

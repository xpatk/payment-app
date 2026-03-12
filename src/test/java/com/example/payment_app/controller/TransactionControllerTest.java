package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.TransactionService;
import com.example.payment_app.service.UserConnectionService;
import com.example.payment_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com")).thenReturn(user);
        when(transactionService.getAllUserTransactions(user)).thenReturn(List.of());
        when(userConnectionService.getConnectionsForUser(user)).thenReturn(List.of());

        mockMvc.perform(get("/transactions"))
                .andExpect(status().isOk())
                .andExpect(view().name("transactions"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldSendMoney() throws Exception {

        User sender = new User();
        sender.setUserId(1);

        User receiver = new User();
        receiver.setUserId(2);

        when(userService.findByEmail("user@mail.com")).thenReturn(sender);
        when(userService.findByEmail("friend@mail.com")).thenReturn(receiver);

        mockMvc.perform(post("/transactions")
                        .with(csrf())
                        .param("receiverEmail","friend@mail.com")
                        .param("description","Dinner")
                        .param("amount","20"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/transactions"));
    }
}
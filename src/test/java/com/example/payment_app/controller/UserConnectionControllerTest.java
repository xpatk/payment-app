package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.UserConnectionService;
import com.example.payment_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserConnectionController.class)
class UserConnectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserConnectionService userConnectionService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnConnectionsPage() throws Exception {

        User user = new User();
        user.setUserId(1);

        User friend = new User();
        friend.setUserId(2);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);
        when(userConnectionService.getConnectionsForUser(user))
                .thenReturn(List.of(friend));

        mockMvc.perform(get("/connections"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections"))
                .andExpect(model().attributeExists("connections"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldAddConnectionSuccessfully() throws Exception {

        User user = new User();
        user.setUserId(1);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/connections/add")
                        .with(csrf())
                        .param("email","friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connections"));

        verify(userConnectionService)
                .saveConnection(user,"friend@mail.com");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldDeleteConnection() throws Exception {

        User user = new User();
        user.setUserId(1);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/connections/delete")
                        .with(csrf())
                        .param("email","friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connections"));

        verify(userConnectionService)
                .deleteConnection(user,"friend@mail.com");
    }
}
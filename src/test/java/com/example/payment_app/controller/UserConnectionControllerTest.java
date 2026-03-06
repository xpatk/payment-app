package com.example.payment_app.controller;
import com.example.payment_app.model.User;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(UserConnectionControllerTest.class)
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

        User friend2 = new User();
        friend2.setUserId(3);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);
        when(userConnectionService.getConnectionsForUser(user))
                .thenReturn(List.of(friend, friend2));

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
                        .param("email", "friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connections"));

        verify(userConnectionService)
                .saveConnection(user, "friend@mail.com");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnConnectionsWhenAddFails() throws Exception {

        User user = new User();
        user.setUserId(1);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        doThrow(new IllegalArgumentException("Already exists"))
                .when(userConnectionService)
                .saveConnection(user, "friend@mail.com");

        mockMvc.perform(post("/connections/add")
                        .param("email", "friend@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("connections"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldDeleteConnection() throws Exception {

        User user = new User();
        user.setUserId(1);

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/connections/delete")
                        .param("email", "friend@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/connections"));

        verify(userConnectionService)
                .deleteConnection(user, "friend@mail.com");
    }
}
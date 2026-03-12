package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.SecurityService;
import com.example.payment_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private SecurityService securityService;

    private User createUser() {
        User user = new User();
        user.setUserId(1);
        user.setUsername("user");
        user.setEmail("user@mail.com");
        return user;
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnProfilePage() throws Exception {

        User user = createUser();

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldUpdateProfile() throws Exception {

        User user = createUser();

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/profile/update")
                        .with(csrf())
                        .param("username", "newUser")
                        .param("email", "new@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(userService).updateUser(1, "newUser", "new@mail.com");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnProfileWhenUpdateFails() throws Exception {

        User user = createUser();

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        doThrow(new IllegalArgumentException("Username exists"))
                .when(userService)
                .updateUser(1, "newUser", "new@mail.com");

        mockMvc.perform(post("/profile/update")
                        .with(csrf())
                        .param("username", "newUser")
                        .param("email", "new@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldUpdatePassword() throws Exception {

        User user = createUser();

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(post("/profile/password")
                        .with(csrf())
                        .param("password", "newPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?passwordChanged"));

        verify(userService).updatePassword(1, "newPass");
    }

    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnEditProfilePage() throws Exception {

        User user = createUser();

        when(userService.findByEmail("user@mail.com")).thenReturn(user);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("user"));
    }
}
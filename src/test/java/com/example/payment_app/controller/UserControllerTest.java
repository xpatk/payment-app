package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.SecurityService;
import com.example.payment_app.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
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

    /**
     * Should display profile page for authenticated user.
     */
    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnProfilePage() throws Exception {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(user);

        mockMvc.perform(get("/profile"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("user"));

    }

    /**
     * Should redirect to login when authentication is missing.
     */
    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {

        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    /**
     * Should update profile and redirect to profile page.
     */
    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldUpdateProfile() throws Exception {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(user);

        mockMvc.perform(post("/profile/update")
                        .param("username", "newUser")
                        .param("email", "new@mail.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile"));

        verify(userService)
                .updateUser(1, "newUser", "new@mail.com");
    }

    /**
     * Should return profile page when update fails.
     */
    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnProfileWhenUpdateFails() throws Exception {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(user);

        doThrow(new IllegalArgumentException("Username exists"))
                .when(userService)
                .updateUser(1, "newUser", "new@mail.com");

        mockMvc.perform(post("/profile/update")
                        .param("username", "newUser")
                        .param("email", "new@mail.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile"))
                .andExpect(model().attributeExists("error"));
    }

    /**
     * Should update password and redirect to login.
     */
    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldUpdatePassword() throws Exception {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(user);

        mockMvc.perform(post("/profile/password")
                        .param("password", "newPass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?passwordChanged"));

        verify(userService).updatePassword(1, "newPass");
    }

    /**
     * Should display profile edit page.
     */
    @Test
    @WithMockUser(username = "user@mail.com")
    void shouldReturnEditProfilePage() throws Exception {

        User user = new User();
        user.setUserId(1);
        user.setEmail("user@mail.com");

        when(userService.findByEmail("user@mail.com"))
                .thenReturn(user);

        mockMvc.perform(get("/profile/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("profile-edit"))
                .andExpect(model().attributeExists("user"));
    }
    }

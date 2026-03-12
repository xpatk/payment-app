package com.example.payment_app.service;

import com.example.payment_app.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityServiceTest {

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private SecurityService securityService;


    @Test
    void shouldRefreshAuthentication() {

        User user = new User();
        user.setEmail("new@mail.com");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("new@mail.com")
                .password("pass")
                .authorities("USER")
                .build();

        Authentication currentAuth =
                new UsernamePasswordAuthenticationToken(
                        "old@mail.com",
                        "pass",
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(currentAuth);

        when(userDetailsService.loadUserByUsername("new@mail.com"))
                .thenReturn(userDetails);

        securityService.refreshAuthentication(user);

        Authentication newAuth = SecurityContextHolder.getContext().getAuthentication();

        assertThat(newAuth.getName()).isEqualTo("new@mail.com");
    }

    /**
     * Should return current authenticated user email.
     */
    @Test
    void shouldReturnCurrentUserEmail() {

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        "user@mail.com",
                        "pass",
                        List.of()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String email = securityService.getCurrentUserEmail();

        assertThat(email).isEqualTo("user@mail.com");
    }

    /**
     * Should return null when no authentication is present.
     */
    @Test
    void shouldReturnNullWhenNoUserAuthenticated() {

        SecurityContextHolder.clearContext();

        String email = securityService.getCurrentUserEmail();

        assertThat(email).isNull();
    }
}

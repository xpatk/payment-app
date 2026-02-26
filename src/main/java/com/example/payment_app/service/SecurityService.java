package com.example.payment_app.service;

import com.example.payment_app.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Service responsible for interacting with Spring Security context.
 *
 * Allows:
 * - retrieving current authenticated user
 * - refreshing authentication after user data changes
 */
@Service
public class SecurityService {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * Refreshes the Spring Security authentication context
     * after user data (e.g. email) has been updated.
     *
     * @param updatedUser the updated user entity
     */
    public void refreshAuthentication(User updatedUser) {

        Authentication currentAuth =
                SecurityContextHolder.getContext().getAuthentication();

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(updatedUser.getEmail());

        UsernamePasswordAuthenticationToken newAuth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        currentAuth.getCredentials(),
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    /**
     * Returns the currently authenticated username (email).
     *
     * @return authenticated email or null if not authenticated
     */
    public String getCurrentUserEmail() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }

        return null;
    }
}
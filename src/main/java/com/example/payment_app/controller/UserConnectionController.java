package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.UserConnectionService;
import com.example.payment_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for managing user connections.
 *
 * Allows:
 * - Viewing connections
 * - Adding a new connection
 * - Removing a connection
 */
@Controller
public class UserConnectionController {

    @Autowired
    private UserConnectionService userConnectionService;

    @Autowired
    private UserService userService;

    /**
     * Displays all connections of the current user.
     */
    @GetMapping("/connections")
    public String showConnections(Authentication authentication, Model model) {

        User user = userService.findByEmail(authentication.getName());

        model.addAttribute("connections",
                userConnectionService.getConnectionsForUser(user));

        return "connections";
    }

    /**
     * Adds a new connection by email.
     */
    @PostMapping("/connections/add")
    public String addConnection(
            @RequestParam String email,
            Authentication authentication,
            Model model
    ) {
        try {
            User user = userService.findByEmail(authentication.getName());

            userConnectionService.saveConnection(user, email);

            return "redirect:/connections";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "connections";
        }
    }

    /**
     * Deletes a connection by email.
     */
    @PostMapping("/connections/delete")
    public String deleteConnection(
            @RequestParam String email,
            Authentication authentication
    ) {

        User user = userService.findByEmail(authentication.getName());

        userConnectionService.deleteConnection(user, email);

        return "redirect:/connections";
    }
}
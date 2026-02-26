package com.example.payment_app.controller;

import com.example.payment_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller responsible for authentication flow;
 * - login page
 * - registration page
 * - user registration process
 *
 * Authentication itself is handled by Spring Security
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Displays login page.
     *
     * Spring Security handles POST /login automatically.
     *
     * @return login view
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Redirects root URL to login page.
     *
     * @return redirect to /login
     */
    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    /**
     * Displays registration page.
     *
     * @return register view
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Handles user registration.
     *
     * @param username chosen username
     * @param email user email
     * @param password raw password (will be encoded)
     * @return redirect to login page after successful registration
     */
    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        try {
            userService.registerUser(username, email, password);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}
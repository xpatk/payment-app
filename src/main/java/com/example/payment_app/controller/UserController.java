package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.UserService;
import com.example.payment_app.service.SecurityService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for user profile management.
 *
 * Handles:
 * - displaying user profile
 * - updating username and email
 * - changing password (with forced logout)
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SecurityService securityService;

    /**
     * Displays currently authenticated user's profile.
     *
     * @param authentication Spring Security authentication object
     * @param model
     * @return profile view
     */
    @GetMapping("/profile")
    public String showProfile(Authentication authentication, Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName());
        model.addAttribute("user", user);

        return "profile";
    }

    /**
     * Updates username and email.
     *
     * If email is changed, SecurityContext is refreshed
     * to keep the session consistent.
     */
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam String username,
            @RequestParam String email,
            Authentication authentication,
            Model model
    ) {

        try {
            User user = userService.findByEmail(authentication.getName());

            userService.updateUser(user.getUserId(), username, email);

            return "redirect:/profile";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }

    /**
     * Updates user password.
     *
     * After successful password change, the user is forcibly logged out
     * to ensure session integrity and security.
     */
    @PostMapping("/profile/password")
    public String updatePassword(
            @RequestParam String password,
            Authentication authentication,
            HttpServletRequest request,
            Model model
    ) {

        try {
            User user = userService.findByEmail(authentication.getName());

            userService.updatePassword(user.getUserId(), password);

            // Clear authentication
            SecurityContextHolder.clearContext();

            // Invalidate HTTP session
            request.getSession().invalidate();

            return "redirect:/login?passwordChanged";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "profile";
        }
    }
}
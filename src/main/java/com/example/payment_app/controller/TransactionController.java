package com.example.payment_app.controller;

import com.example.payment_app.model.User;
import com.example.payment_app.service.TransactionService;
import com.example.payment_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsible for handling transaction views and money transfers.
 *
 * Responsibilities:
 * - Displaying current user's transaction history
 * - Processing money transfer requests
 *
 * Business logic is delegated to TransactionService.
 */
@Controller
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private UserService userService;

    /**
     * Displays all transactions related to the currently authenticated user.
     *
     * @param authentication Spring Security authentication object
     * @param model          Spring MVC model used to pass data to the view
     * @return transactions view
     */
    @GetMapping("/transactions")
    public String showTransactions(Authentication authentication, Model model) {

        if (authentication == null) {
            return "redirect:/login";
        }
        User user = userService.findByEmail(authentication.getName());

        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("transactions",
                transactionService.getAllUserTransactions(user));

        return "transactions";
    }

    /**
     * Handles money transfer between users.
     *
     * @param receiverEmail email of the receiving user
     * @param amount        amount to transfer
     * @param authentication Spring Security authentication object
     * @param model         model for error messages
     * @return redirect to transactions page
     */
    @PostMapping("/transactions")
    public String sendMoney(
            @RequestParam String receiverEmail,
            @RequestParam Double amount,
            Authentication authentication,
            Model model
    ) {

        try {
            User sender = userService.findByEmail(authentication.getName());
            User receiver = userService.findByEmail(receiverEmail);

            transactionService.sendMoney(sender, receiver, amount);

            return "redirect:/transactions";

        } catch (IllegalArgumentException e) {

            model.addAttribute("error", e.getMessage());

            User user = userService.findByEmail(authentication.getName());
            model.addAttribute("transactions",
                    transactionService.getAllUserTransactions(user));

            return "transactions";
        }
    }
}
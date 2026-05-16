package com.memzocards.controller;

import com.memzocards.service.UserService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Validated
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam @Email @NotBlank String email,
                           @RequestParam @NotBlank @Size(min = 6, max = 100) String password,
                           Model model) {
        boolean created = userService.register(email, password);
        if (!created) {
            model.addAttribute("error", "Email is already registered.");
            return "register";
        }
        return "redirect:/login?registered";
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam @Email @NotBlank String email, Model model) {
        userService.createResetTokenForEmail(email).ifPresentOrElse(
                token -> {
                    model.addAttribute("message", "Password reset token generated. Use it below.");
                    model.addAttribute("token", token);
                },
                () -> model.addAttribute("error", "No user with that email was found.")
        );
        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String token, Model model) {
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam @NotBlank String token,
                                @RequestParam @NotBlank @Size(min = 6, max = 100) String password,
                                Model model) {
        boolean success = userService.resetPassword(token, password);
        if (!success) {
            model.addAttribute("error", "Invalid or expired reset token.");
            model.addAttribute("token", token);
            return "reset-password";
        }
        return "redirect:/login?resetSuccess";
    }
}

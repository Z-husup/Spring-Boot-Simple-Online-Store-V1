package com.example.springsimplestorev1.infrastructure.web;

import com.example.springsimplestorev1.application.usecase.user.RegisterUserUseCase;
import com.example.springsimplestorev1.domain.exception.DuplicateResourceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;
    private final PasswordEncoder passwordEncoder;

    public AuthController(RegisterUserUseCase registerUserUseCase, PasswordEncoder passwordEncoder) {
        this.registerUserUseCase = registerUserUseCase;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String email,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String confirmPassword
    ) {
        if (!password.equals(confirmPassword)) {
            return "redirect:/register?error=password_mismatch";
        }

        try {
            registerUserUseCase.execute(email, name, passwordEncoder.encode(password));
        } catch (DuplicateResourceException ex) {
            return "redirect:/register?error=email_exists";
        }
        return "redirect:/login?registered";
    }
}

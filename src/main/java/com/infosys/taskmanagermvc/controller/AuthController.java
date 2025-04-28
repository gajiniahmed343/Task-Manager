package com.infosys.taskmanagermvc.controller;

import com.infosys.taskmanagermvc.entity.User;
import com.infosys.taskmanagermvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
@Controller
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder pwdEncoder;

    // Display login page
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // returns login.html (Thymeleaf template)
    }

    // Display registration page
    @GetMapping("/register")
    public String registerPage() {
        return "register"; // returns register.html (Thymeleaf template)
    }

    // Handle registration submission
    @PostMapping("/register")
    public String registration(User user, Model model) {
        // Encode the password before saving the user
        String encodedPwd = pwdEncoder.encode(user.getPassword());
        user.setPassword(encodedPwd);

        // Save the user to the repository
        repo.save(user);

        // Add a success message and redirect to the login page
        model.addAttribute("success", "User registered successfully. Please log in.");
        return "login"; // Redirect to login page after successful registration
    }
}
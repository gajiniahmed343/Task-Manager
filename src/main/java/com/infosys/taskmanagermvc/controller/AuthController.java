package com.infosys.taskmanagermvc.controller;

import com.infosys.taskmanagermvc.entity.User;
import com.infosys.taskmanagermvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder pwdEncoder;

    // Display login page
    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "success", required = false) String success, Model model) {
        if (success != null && success.equals("true")) {
            model.addAttribute("success", "Registration successful! Please log in.");
        }
        return "login"; // returns login.html (Thymeleaf template)
    }

    // Display registration page
    @GetMapping("/register")
    public String registerPage() {
        return "register"; // returns register.html (Thymeleaf template)
    }

    // Handle registration submission
    @PostMapping("/register")
    public String registration(@ModelAttribute User user,@RequestParam("confirmPassword") String confirmPassword, Model model) {
        // Check if the email already exists in the database
        if (repo.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email already exists. Please use a different email.");
            return "register"; // Return to registration page with error message
        }

        // Check if password and confirm password match
        if (!user.getPassword().equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match. Please try again.");
            return "register"; // Return to registration page with password mismatch error
        }

        // Encode the password before saving the user
        String encodedPwd = pwdEncoder.encode(user.getPassword());
        user.setPassword(encodedPwd);

        user.setRole("USER");
        // Save the user to the repository
        repo.save(user);

        // Redirect to login page with success message
        return "redirect:/login?success=true"; // Redirect to login page after successful registration
    }
}

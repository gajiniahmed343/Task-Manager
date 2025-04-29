package com.infosys.taskmanagermvc.controller;

import org.springframework.ui.Model;
import com.infosys.taskmanagermvc.entity.User;
import com.infosys.taskmanagermvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserRepository userRepository;

    @ModelAttribute
    public void addFullNameToModel(Model model, Principal principal) {
        if (principal != null) {
            Optional<User> userOptional = userRepository.findByEmail(principal.getName());

            // Add the full name if the user is present
            userOptional.ifPresent(user -> model.addAttribute("fullname", user.getFullname()));
        }
    }
}

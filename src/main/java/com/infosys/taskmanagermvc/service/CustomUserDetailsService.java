package com.infosys.taskmanagermvc.service;

import com.infosys.taskmanagermvc.entity.User;
import com.infosys.taskmanagermvc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email: " + email + " not found"));

        // Assuming the role is 'USER' for all users, we will assign 'ROLE_USER' to the user
        // If you store roles in your database, you can fetch them dynamically as well
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());


        // Map User entity to UserDetails object for Spring Security with ROLE_USER
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(authority) // Assign ROLE_USER to the user
        );
    }
}

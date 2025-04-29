package com.infosys.taskmanagermvc.config;

import com.infosys.taskmanagermvc.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder pwdEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(pwdEncoder());
        return authProvider;
    }

    // PersistentTokenRepository bean - used for remember-me functionality
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        return new InMemoryTokenRepositoryImpl();  // Using in-memory token repository (for simplicity)
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/", "/login", "/register").permitAll()  // Public routes
                                .requestMatchers("/tasks/**").authenticated()  // Task routes require authentication
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .usernameParameter("email") // Use email as username
                                .defaultSuccessUrl("/tasks", true)
                                .permitAll()
                )
                .rememberMe(rememberMe ->
                        rememberMe
                                .tokenRepository(persistentTokenRepository())  // Set the token repository
                                .key("uniqueAndSecret")  // Specify a unique key (it must match when validating token)
                                .rememberMeParameter("remember-me")  // Name of the checkbox in the login form
                                .tokenValiditySeconds(86400)  // Validity of the token (in seconds) - 1 day here
                )
                .logout(logout ->
                        logout
                                .logoutUrl("/logout") // Path for logout request
                                .logoutSuccessUrl("/login?logout=true")  // Redirect after successful logout
                                .invalidateHttpSession(true)  // Invalidate session
                                .clearAuthentication(true)    // Clear authentication
                                .permitAll()
                );
        return http.build();
    }
}

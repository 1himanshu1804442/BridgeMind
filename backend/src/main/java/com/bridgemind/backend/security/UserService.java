package com.bridgemind.backend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// Service layer for user management. Implements UserDetailsService so Spring Security
// can load users during authentication. Sits between AuthController and UserRepository.
@Service
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection — makes dependencies explicit and immutable
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Registers a new user by hashing their password and persisting to the database.
    // Throws IllegalArgumentException if the email is already taken — the controller
    // returns this as a 409 Conflict.
    public User register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Registration attempt with existing email: {}", email);
            throw new IllegalArgumentException("Email already registered: " + email);
        }

        User user = new User(email, passwordEncoder.encode(password));
        User saved = userRepository.save(user);
        log.info("New user registered: {} with role: {}", saved.getEmail(), saved.getRole());
        return saved;
    }

    // Required by UserDetailsService — Spring Security calls this during authentication.
    // We map our User entity to Spring's UserDetails, prefixing the role with "ROLE_"
    // because Spring Security expects that prefix for hasRole() checks.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("User not found: " + email);
                });

        log.info("Loaded user from database: {}", email);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}

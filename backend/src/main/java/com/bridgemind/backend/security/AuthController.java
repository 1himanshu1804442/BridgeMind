package com.bridgemind.backend.security;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// REST controller for authentication endpoints. Handles user registration and login.
// These endpoints are publicly accessible (configured in SecurityConfig).
// Business logic is delegated to UserService — this controller only handles
// HTTP request/response concerns.
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor injection — all dependencies are explicit and immutable
    public AuthController(UserService userService, JwtService jwtService,
                          AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // POST /api/auth/register — creates a new user and returns a JWT token.
    // The user is immediately authenticated after registration (no separate login needed).
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
        log.info("Registration request for email: {}", request.getEmail());
        try {
            userService.register(request.getEmail(), request.getPassword());

            // Load the full UserDetails to generate a token with proper authorities
            UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            log.info("User registered successfully: {}", request.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
        } catch (IllegalArgumentException e) {
            // Email already exists — return 409 Conflict
            log.warn("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // POST /api/auth/login — authenticates credentials and returns a JWT token.
    // Uses Spring's AuthenticationManager which delegates to our DaoAuthenticationProvider.
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());
        try {
            // AuthenticationManager handles password verification via BCrypt
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));

            UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
            String token = jwtService.generateToken(userDetails);

            log.info("User logged in successfully: {}", request.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (BadCredentialsException e) {
            log.warn("Login failed for email: {} — bad credentials", request.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }
}

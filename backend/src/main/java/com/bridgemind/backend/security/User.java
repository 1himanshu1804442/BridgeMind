package com.bridgemind.backend.security;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

// JPA entity for the 'users' table. Stores authentication credentials and role info.
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Email serves as the unique username for Spring Security's UserDetailsService
    @Column(nullable = false, unique = true)
    private String email;

    // BCrypt-hashed password — never store plaintext
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    // Role enum controls authorization level (ADMIN vs USER)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public enum Role {
        ADMIN, USER
    }

    // JPA requires a no-arg constructor
    protected User() {}

    public User(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
    }

    // Auto-set createdAt before first persist to avoid null timestamps
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    // --- Getters and Setters ---
    public UUID getId() { return id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public Instant getCreatedAt() { return createdAt; }
}

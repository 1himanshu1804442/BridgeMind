package com.bridgemind.backend.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// DTO for login and register requests. Validation happens at the controller layer
// via @Valid before the request reaches the service.
public class AuthRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    // Jackson needs a no-arg constructor for deserialization
    public AuthRequest() {}

    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

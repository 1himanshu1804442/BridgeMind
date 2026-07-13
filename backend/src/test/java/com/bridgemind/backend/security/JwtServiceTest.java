package com.bridgemind.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Unit tests for JwtService — no Spring context needed.
// We instantiate JwtService directly with a test secret key and short expiration.
@DisplayName("JwtService Unit Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails testUser;

    // Secret must be at least 256 bits (32 bytes) for HMAC-SHA256
    private static final String TEST_SECRET = "test-secret-key-that-is-at-least-256-bits-long-for-hmac-sha256";
    private static final long TEST_EXPIRATION_MS = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(TEST_SECRET, TEST_EXPIRATION_MS);
        testUser = new User(
                "test@example.com",
                "hashedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("Should generate a non-null, non-empty JWT token")
    void generateToken_shouldReturnNonEmptyToken() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token, "Token should not be null");
        assertFalse(token.isEmpty(), "Token should not be empty");
        // JWT tokens have 3 dot-separated parts: header.payload.signature
        assertEquals(3, token.split("\\.").length, "JWT should have 3 parts separated by dots");
    }

    @Test
    @DisplayName("Should extract the correct username (email) from token")
    void extractUsername_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(testUser);

        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedUsername,
                "Extracted username should match the original user's email");
    }

    @Test
    @DisplayName("Should validate token as valid for the correct user")
    void isTokenValid_shouldReturnTrueForCorrectUser() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid, "Token should be valid for the user it was generated for");
    }

    @Test
    @DisplayName("Should reject token when username does not match")
    void isTokenValid_shouldReturnFalseForDifferentUser() {
        String token = jwtService.generateToken(testUser);

        // Create a different user with a different email
        UserDetails differentUser = new User(
                "other@example.com",
                "hashedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid, "Token should be invalid for a different user");
    }

    @Test
    @DisplayName("Should reject an expired token")
    void isTokenValid_shouldReturnFalseForExpiredToken() {
        // Create a JwtService with 0ms expiration — token expires immediately
        JwtService expiredJwtService = new JwtService(TEST_SECRET, 0);
        String token = expiredJwtService.generateToken(testUser);

        // Small delay to ensure the token is past its expiration
        try { Thread.sleep(10); } catch (InterruptedException ignored) {}

        boolean isValid = expiredJwtService.isTokenValid(token, testUser);

        assertFalse(isValid, "Expired token should not be valid");
    }

    @Test
    @DisplayName("Should reject a tampered/malformed token")
    void isTokenValid_shouldReturnFalseForTamperedToken() {
        String token = jwtService.generateToken(testUser);
        // Tamper with the token by appending characters to the signature
        String tamperedToken = token + "tampered";

        boolean isValid = jwtService.isTokenValid(tamperedToken, testUser);

        assertFalse(isValid, "Tampered token should not be valid");
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void generateToken_shouldCreateUniqueTokensPerUser() {
        UserDetails anotherUser = new User(
                "another@example.com",
                "hashedPassword",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(anotherUser);

        assertNotEquals(token1, token2,
                "Tokens for different users should be different");
    }
}

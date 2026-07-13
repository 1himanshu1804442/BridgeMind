package com.bridgemind.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

// Service responsible for all JWT token operations: generation, parsing, and validation.
// Uses HMAC-SHA256 for signing. The secret key and expiration are externalized to config.
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey signingKey;
    private final long expirationMs;

    // Constructor injection with @Value — the secret is read from application.yml.
    // We derive the HMAC key once at startup to avoid re-creating it on every call.
    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        log.info("JwtService initialized with expiration={}ms", expirationMs);
    }

    // Generates a JWT token with the user's email (username) as the subject.
    // The token includes issued-at and expiration claims.
    public String generateToken(UserDetails userDetails) {
        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(signingKey)
                .compact();
        log.info("Generated JWT token for user: {}", userDetails.getUsername());
        return token;
    }

    // Extracts the username (email) from the token's subject claim.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Validates the token by checking: (1) the username matches and (2) token is not expired.
    // Returns false instead of throwing on invalid tokens — the filter uses this to decide
    // whether to authenticate the request.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            boolean valid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            if (!valid) {
                log.warn("Token validation failed for user: {}", userDetails.getUsername());
            }
            return valid;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    // Generic claim extractor — avoids code duplication when extracting different claims.
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}

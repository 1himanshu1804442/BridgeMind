package com.bridgemind.backend.security;

// Immutable response DTO using Java record — contains only the JWT token.
// Records are ideal for DTOs because they're concise, immutable, and auto-generate
// equals/hashCode/toString — which is exactly what we need for API responses.
public record AuthResponse(String token) {}

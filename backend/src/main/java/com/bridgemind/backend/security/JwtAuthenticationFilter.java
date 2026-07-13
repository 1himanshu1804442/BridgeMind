package com.bridgemind.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Filter that runs once per request to extract and validate JWT tokens.
// Placed before Spring Security's UsernamePasswordAuthenticationFilter in the chain.
// If a valid JWT is found, it sets the SecurityContext so downstream filters/controllers
// see the request as authenticated.
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtService jwtService;
    private final UserService userService;

    // Constructor injection for testability and immutability
    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        // Extract the Authorization header; skip if missing or not a Bearer token
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Strip "Bearer " prefix to get the raw JWT
        String jwt = authHeader.substring(7);

        try {
            String userEmail = jwtService.extractUsername(jwt);

            // Only authenticate if the user isn't already authenticated in this request.
            // This prevents redundant database lookups if multiple filters are chained.
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(userEmail);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Build the authentication token with the user's authorities
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    // Attach request details (IP, session ID) for audit logging
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authenticated user in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Authenticated user: {} for URI: {}", userEmail, request.getRequestURI());
                }
            }
        } catch (Exception e) {
            // Don't block the request on JWT parsing errors — let Spring Security
            // handle the unauthenticated request (will return 401/403)
            log.error("JWT authentication error: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}

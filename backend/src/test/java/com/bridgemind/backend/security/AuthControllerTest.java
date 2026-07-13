package com.bridgemind.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Integration test for AuthController using @WebMvcTest.
// Only loads the web layer (controller + security filters), mocking the service layer.
// This tests the HTTP request/response behavior and security configuration.
// SecurityConfig is imported because @WebMvcTest doesn't auto-scan @Configuration classes
// outside the controller's package by default — we need it for the security filter chain.
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock all service-layer dependencies that AuthController and SecurityConfig need
    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    // JwtAuthenticationFilter is a @Component that SecurityConfig depends on —
    // we mock it so it doesn't actually try to validate JWTs during tests
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // Helper method to create a consistent test UserDetails object
    private UserDetails createTestUserDetails() {
        return new org.springframework.security.core.userdetails.User(
                "test@example.com",
                "$2a$10$encodedPasswordHash",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }

    @Test
    @DisplayName("POST /api/auth/register — should return 201 with JWT token")
    void register_shouldReturn201WithToken() throws Exception {
        // Arrange: mock the service layer to return a new user and a JWT
        User mockUser = new User("test@example.com", "$2a$10$encodedPasswordHash");
        when(userService.register(anyString(), anyString())).thenReturn(mockUser);
        when(userService.loadUserByUsername("test@example.com")).thenReturn(createTestUserDetails());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mock-jwt-token");

        AuthRequest request = new AuthRequest("test@example.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    @DisplayName("POST /api/auth/register — should return 409 for duplicate email")
    void register_shouldReturn409ForDuplicateEmail() throws Exception {
        // Arrange: simulate email already exists
        when(userService.register(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Email already registered: test@example.com"));

        AuthRequest request = new AuthRequest("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("POST /api/auth/register — should return 400 for missing email")
    void register_shouldReturn400ForMissingEmail() throws Exception {
        // Arrange: send request with blank email to trigger @NotBlank validation
        AuthRequest request = new AuthRequest("", "password123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/auth/login — should return 200 with JWT token")
    void login_shouldReturn200WithToken() throws Exception {
        // Arrange: mock successful authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken("test@example.com", null));
        when(userService.loadUserByUsername("test@example.com")).thenReturn(createTestUserDetails());
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mock-jwt-token");

        AuthRequest request = new AuthRequest("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"));
    }

    @Test
    @DisplayName("POST /api/auth/login — should return 401 for bad credentials")
    void login_shouldReturn401ForBadCredentials() throws Exception {
        // Arrange: mock failed authentication
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        AuthRequest request = new AuthRequest("test@example.com", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }

    @Test
    @DisplayName("POST /api/auth/login — should return 400 for missing password")
    void login_shouldReturn400ForMissingPassword() throws Exception {
        // Arrange: send request with blank password
        AuthRequest request = new AuthRequest("test@example.com", "");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

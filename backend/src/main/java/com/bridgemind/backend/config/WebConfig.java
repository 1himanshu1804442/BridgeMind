package com.bridgemind.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// CORS is now managed by SecurityConfig.corsConfigurationSource() instead of here.
// Spring Security processes requests before the MVC layer, so CORS must be configured
// at the security level to properly handle preflight OPTIONS requests.
// This class is kept as a placeholder for any future MVC-specific configuration
// (e.g., custom formatters, interceptors, view resolvers).
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // CORS configuration has been moved to SecurityConfig to avoid conflicts.
    // When both WebMvcConfigurer and SecurityFilterChain define CORS, the security
    // layer takes precedence — which can cause unexpected 403s on preflight requests
    // if the configs are out of sync.
}

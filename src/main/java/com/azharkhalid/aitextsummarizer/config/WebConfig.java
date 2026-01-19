package com.azharkhalid.aitextsummarizer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration for Cross-Origin Resource Sharing (CORS).
 * Controls which domains can access the API.
 */
@Slf4j
@Configuration
public class WebConfig {

    /**
     * Configures CORS filter for the application.
     *
     * In production, you should:
     * - Restrict allowed origins to specific domains
     * - Disable wildcard origins
     * - Use environment variables for origin configuration
     *
     * @return CorsFilter with configured settings
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();

        // Configure allowed origins
        // For development: allow all origins
        // For production: use specific origins from environment
        String allowedOrigins = System.getenv().getOrDefault(
                "CORS_ALLOWED_ORIGINS",
                "http://localhost:3000,http://localhost:8080"
        );

        if ("*".equals(allowedOrigins)) {
            log.warn("CORS configured to allow all origins - NOT RECOMMENDED FOR PRODUCTION");
            config.addAllowedOriginPattern("*");
        } else {
            List<String> origins = Arrays.asList(allowedOrigins.split(","));
            origins.forEach(config::addAllowedOrigin);
            log.info("CORS configured for origins: {}", origins);
        }

        // Allowed HTTP methods
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allowed headers
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // Exposed headers
        config.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // Max age for preflight requests (1 hour)
        config.setMaxAge(3600L);

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}

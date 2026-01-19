package com.azharkhalid.aitextsummarizer.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuration for adding security headers to all HTTP responses.
 * These headers help protect against various security vulnerabilities.
 */
@Slf4j
@Configuration
public class SecurityHeadersConfig implements WebMvcConfigurer {

    /**
     * Registers an interceptor that adds security headers to every response.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityHeadersInterceptor());
    }

    /**
     * Interceptor that adds security headers to HTTP responses.
     */
    private static class SecurityHeadersInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler) {

            // Prevent clickjacking attacks
            response.setHeader("X-Frame-Options", "DENY");

            // Prevent MIME type sniffing
            response.setHeader("X-Content-Type-Options", "nosniff");

            // Enable XSS protection (legacy browsers)
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // Content Security Policy (basic version)
            // For APIs, this can be relatively permissive
            response.setHeader("Content-Security-Policy",
                    "default-src 'none'; " +
                    "frame-ancestors 'none'");

            // Referrer policy
            response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

            // Remove server information (security through obscurity)
            response.setHeader("Server", "");

            log.debug("Security headers added to response");

            return true;
        }
    }
}

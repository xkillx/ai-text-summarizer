package com.azharkhalid.aitextsummarizer.service;

import com.azharkhalid.aitextsummarizer.exception.RateLimitExceededException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service that applies rate limiting to the summarize API.
 * Uses Resilience4j RateLimiter to prevent abuse and ensure fair usage.
 */
@Slf4j
@Service
public class RateLimitingService {

    private static final String RATE_LIMITER_NAME = "summarizeApi";

    /**
     * Checks if the request is within rate limits.
     * Throws RateLimitExceededException if limit is exceeded.
     *
     * @throws RateLimitExceededException if rate limit is exceeded
     */
    @RateLimiter(name = RATE_LIMITER_NAME, fallbackMethod = "rateLimitFallback")
    public void checkRateLimit() {
        log.debug("Request within rate limit");
        // If we reach here, the request is within the rate limit
    }

    /**
     * Fallback method when rate limit is exceeded.
     *
     * @param exception The exception that triggered the fallback
     * @throws RateLimitExceededException always
     */
    private void rateLimitFallback(Exception exception) {
        log.warn("Rate limit exceeded: {}", exception.getMessage());
        throw new RateLimitExceededException(
                "Rate limit exceeded. Maximum 10 requests per minute allowed. " +
                        "Please try again later."
        );
    }
}

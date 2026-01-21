package com.azharkhalid.aitextsummarizer.config;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration for Resilience4j patterns (Retry, TimeLimiter).
 * Provides resilience against transient failures and enforces timeouts.
 */
@Slf4j
@Configuration
public class ResilienceConfig {

    /**
     * Configures the retry mechanism for LLM calls.
     * Uses exponential backoff to avoid overwhelming the service during outages.
     */
    @Bean
    public Retry summarizeRetry() {
        // Custom interval function with exponential backoff
        IntervalFunction intervalFunction = IntervalFunction
                .ofExponentialBackoff(Duration.ofSeconds(1), 2.0);

        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3)
                .intervalFunction(intervalFunction)
                .retryExceptions(Exception.class)
                .ignoreExceptions(com.azharkhalid.aitextsummarizer.exception.InvalidInputException.class)
                .build();

        return Retry.of("summarizeService", config);
    }

    /**
     * Configures the time limiter for LLM calls.
     * Ensures requests don't hang indefinitely.
     */
    @Bean
    public TimeLimiter summarizeTimeLimiter() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                .timeoutDuration(Duration.ofSeconds(30))
                .cancelRunningFuture(true)
                .build();

        return TimeLimiter.of("summarizeService", config);
    }
}

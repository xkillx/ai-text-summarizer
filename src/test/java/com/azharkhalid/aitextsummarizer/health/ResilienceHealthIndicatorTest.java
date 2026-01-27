package com.azharkhalid.aitextsummarizer.health;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResilienceHealthIndicator Tests")
class ResilienceHealthIndicatorTest {

    private ResilienceHealthIndicator healthIndicator;

    @BeforeEach
    void setUp() {
        RetryRegistry retryRegistry = RetryRegistry.ofDefaults();
        TimeLimiterRegistry timeLimiterRegistry = TimeLimiterRegistry.ofDefaults();
        RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();

        healthIndicator = new ResilienceHealthIndicator(
                retryRegistry,
                timeLimiterRegistry,
                rateLimiterRegistry
        );
    }

    @Test
    @DisplayName("Should return UP status with resilience details")
    void shouldReturnUpStatusWithResilienceDetails() {
        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).isNotNull();
    }
}

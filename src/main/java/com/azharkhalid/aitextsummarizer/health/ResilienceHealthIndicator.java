package com.azharkhalid.aitextsummarizer.health;

import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Health indicator for Resilience4j components.
 * Reports the status of retry, timelimiter, and ratelimiter.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ResilienceHealthIndicator implements HealthIndicator {

    private final RetryRegistry retryRegistry;
    private final TimeLimiterRegistry timeLimiterRegistry;
    private final RateLimiterRegistry rateLimiterRegistry;

    @Override
    public Health health() {
        Map<String, Object> details = new HashMap<>();

        try {
            // Check Retry status
            var retry = retryRegistry.getAllRetries().stream()
                    .filter(r -> r.getName().equals("summarizeService"))
                    .findFirst();

            if (retry.isPresent()) {
                var retryConfig = retry.get();
                details.put("retry", Map.of(
                        "name", retryConfig.getName(),
                        "maxAttempts", retryConfig.getRetryConfig().getMaxAttempts()
                ));
            }

            // Check TimeLimiter status
            var timeLimiter = timeLimiterRegistry.getAllTimeLimiters().stream()
                    .filter(t -> t.getName().equals("summarizeService"))
                    .findFirst();

            if (timeLimiter.isPresent()) {
                var tlConfig = timeLimiter.get();
                details.put("timeLimiter", Map.of(
                        "name", tlConfig.getName(),
                        "timeout", tlConfig.getTimeLimiterConfig().getTimeoutDuration().toMillis() + "ms"
                ));
            }

            // Check RateLimiter status
            var rateLimiter = rateLimiterRegistry.getAllRateLimiters().stream()
                    .filter(r -> r.getName().equals("summarizeApi"))
                    .findFirst();

            if (rateLimiter.isPresent()) {
                var rlConfig = rateLimiter.get();
                var metrics = rlConfig.getMetrics();
                details.put("rateLimiter", Map.of(
                        "name", rlConfig.getName(),
                        "limitForPeriod", rlConfig.getRateLimiterConfig().getLimitForPeriod(),
                        "refreshPeriod", rlConfig.getRateLimiterConfig().getLimitRefreshPeriod().toMillis() + "ms",
                        "availablePermissions", metrics.getAvailablePermissions(),
                        "numberOfWaitingThreads", metrics.getNumberOfWaitingThreads()
                ));
            }

            return Health.up()
                    .withDetails(details)
                    .build();

        } catch (Exception e) {
            log.error("Resilience health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}

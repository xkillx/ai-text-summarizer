package com.azharkhalid.aitextsummarizer.config;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResilienceConfig Tests")
@SpringBootTest
class ResilienceConfigTest {

    @Autowired(required = false)
    private Retry summarizeRetry;

    @Autowired(required = false)
    private TimeLimiter summarizeTimeLimiter;

    @Test
    @DisplayName("Should create Retry bean with correct configuration")
    void shouldCreateRetryBean() {
        assertThat(summarizeRetry).isNotNull();
        assertThat(summarizeRetry.getName()).isEqualTo("summarizeService");
    }

    @Test
    @DisplayName("Should create TimeLimiter bean with correct configuration")
    void shouldCreateTimeLimiterBean() {
        assertThat(summarizeTimeLimiter).isNotNull();
        assertThat(summarizeTimeLimiter.getName()).isEqualTo("summarizeService");
        assertThat(summarizeTimeLimiter.getTimeLimiterConfig().getTimeoutDuration())
                .isGreaterThan(Duration.ofSeconds(20));
    }
}

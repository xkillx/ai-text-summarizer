package com.azharkhalid.aitextsummarizer.health;

import com.azharkhalid.aitextsummarizer.config.SummarizeProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LLMHealthIndicator Tests")
class LLMHealthIndicatorTest {

    private LLMHealthIndicator healthIndicator;
    private SummarizeProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SummarizeProperties();
        properties.setModel("gpt-4o-mini");
        properties.setTemperature(0.3);
        properties.setMaxTokens(500);
        properties.setTimeout(java.time.Duration.ofSeconds(30));

        healthIndicator = new LLMHealthIndicator(null, properties);
    }

    @Test
    @DisplayName("Should return UP status when model is configured")
    void shouldReturnUpStatusWhenModelConfigured() {
        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails()).containsKey("model");
        assertThat(health.getDetails()).containsKey("temperature");
        assertThat(health.getDetails()).containsKey("maxTokens");
    }

    @Test
    @DisplayName("Should return DOWN status when model is not configured")
    void shouldReturnDownStatusWhenModelNotConfigured() {
        properties.setModel(null);

        Health health = healthIndicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("reason");
    }
}

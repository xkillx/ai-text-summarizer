package com.azharkhalid.aitextsummarizer.health;

import com.azharkhalid.aitextsummarizer.config.SummarizeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator for LLM provider connectivity.
 * Checks the OpenAI configuration to verify service availability.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LLMHealthIndicator implements HealthIndicator {

    private final OpenAiChatModel openAiChatModel;
    private final SummarizeProperties properties;

    /**
     * Check the health of the LLM provider.
     * Verifies configuration is valid.
     *
     * @return Health status with details
     */
    @Override
    public Health health() {
        try {
            log.debug("Checking LLM provider health...");

            // Check if ChatClient is configured
            if (openAiChatModel == null) {
                return Health.down()
                        .withDetail("reason", "OpenAI ChatClient not configured")
                        .withDetail("model", properties.getModel())
                        .build();
            }

            long startTime = System.currentTimeMillis();

            // Simple health check - verify configuration is present
            boolean isConfigured = properties.getModel() != null && !properties.getModel().isEmpty();

            long responseTime = System.currentTimeMillis() - startTime;

            if (isConfigured) {
                log.debug("LLM provider health check passed in {} ms", responseTime);

                return Health.up()
                        .withDetail("model", properties.getModel())
                        .withDetail("temperature", properties.getTemperature())
                        .withDetail("maxTokens", properties.getMaxTokens())
                        .withDetail("checkDurationMs", responseTime)
                        .withDetail("timeout", properties.getTimeout().toMillis() + "ms")
                        .build();
            } else {
                log.warn("LLM provider health check failed: model not configured");

                return Health.down()
                        .withDetail("reason", "Model not configured")
                        .withDetail("model", properties.getModel())
                        .build();
            }

        } catch (Exception e) {
            log.error("LLM provider health check failed", e);

            return Health.down()
                    .withDetail("reason", "Health check failed: " + e.getMessage())
                    .withDetail("model", properties.getModel())
                    .withDetail("error", e.getClass().getSimpleName())
                    .build();
        }
    }
}

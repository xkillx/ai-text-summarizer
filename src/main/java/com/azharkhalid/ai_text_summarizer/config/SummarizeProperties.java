package com.azharkhalid.ai_text_summarizer.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai.summarizer")
public class SummarizeProperties {

    private String model = "gpt-4o-mini";
    private double temperature = 0.3;
    private int maxTokens = 500;
    private Duration timeout = Duration.ofSeconds(30);
    private int maxInputLength = 10000;
    private RetryConfig retry = new RetryConfig();

    @Data
    public static class RetryConfig {
        private int maxAttempts = 3;
        private Duration backoff = Duration.ofSeconds(2);
    }
}

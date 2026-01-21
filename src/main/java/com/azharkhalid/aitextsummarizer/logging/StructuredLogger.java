package com.azharkhalid.aitextsummarizer.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Utility class for structured logging with consistent format.
 * Provides methods for logging with key-value pairs for better observability.
 */
@Slf4j
@Component
public class StructuredLogger {

    /**
     * Log a summarization request with structured data.
     */
    public void logSummarizeRequest(String requestId, int inputLength, String style, Integer maxLength) {
        log.info("summarize_request initiated requestId={} inputLength={} style={} maxLength={}",
                requestId, inputLength, style, maxLength);
    }

    /**
     * Log a successful summarization with structured data.
     */
    public void logSummarizeSuccess(String requestId, int summaryLength, long processingTimeMs, String model) {
        log.info("summarize_request completed requestId={} summaryLength={} processingTimeMs={} model={}",
                requestId, summaryLength, processingTimeMs, model);
    }

    /**
     * Log a failed summarization with structured data.
     */
    public void logSummarizeFailure(String requestId, String errorType, String errorMessage) {
        log.error("summarize_request failed requestId={} errorType={} errorMessage={}",
                requestId, errorType, errorMessage);
    }

    /**
     * Log a retry attempt.
     */
    public void logRetryAttempt(String requestId, int attempt, String reason) {
        log.warn("summarize_request retrying requestId={} attempt={} reason={}",
                requestId, attempt, reason);
    }

    /**
     * Log rate limit exceeded.
     */
    public void logRateLimitExceeded(String clientId) {
        log.warn("rate_limit exceeded clientId={}", clientId);
    }

    /**
     * Log validation error.
     */
    public void logValidationError(String requestId, String field, String constraint) {
        log.warn("validation_error requestId={} field={} constraint={}",
                requestId, field, constraint);
    }

    /**
     * Log with custom key-value pairs.
     */
    public void logEvent(String event, Map<String, Object> data) {
        StringBuilder sb = new StringBuilder(event);
        data.forEach((key, value) -> sb.append(" ").append(key).append("=").append(value));
        log.info(sb.toString());
    }
}

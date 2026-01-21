package com.azharkhalid.aitextsummarizer.service;

import com.azharkhalid.aitextsummarizer.config.SummarizeProperties;
import com.azharkhalid.aitextsummarizer.dto.request.SummarizeRequest;
import com.azharkhalid.aitextsummarizer.dto.response.SummarizeResponse;
import com.azharkhalid.aitextsummarizer.enums.SummaryStyle;
import com.azharkhalid.aitextsummarizer.exception.LLMTimeoutException;
import com.azharkhalid.aitextsummarizer.exception.SummarizerException;
import com.azharkhalid.aitextsummarizer.metrics.SummarizeMetrics;
import com.azharkhalid.aitextsummarizer.util.InputSanitizer;
import com.azharkhalid.aitextsummarizer.validation.CharacterEncodingValidator;
import com.azharkhalid.aitextsummarizer.validation.MaxInputSizeValidator;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Core service for handling text summarization using LLM.
 * Enhanced with resilience patterns (retry, timeout) and metrics tracking.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizeService {

    private final ChatClient chatClient;
    private final PromptService promptService;
    private final SummarizeProperties properties;
    private final MaxInputSizeValidator sizeValidator;
    private final CharacterEncodingValidator encodingValidator;
    private final RateLimitingService rateLimitingService;
    private final SummarizeMetrics metrics;

    /**
     * Summarizes the provided text using the configured LLM.
     * Implements retry logic with exponential backoff and timeout protection.
     *
     * @param request The summarization request containing text and options
     * @return CompletableFuture containing SummarizeResponse
     * @throws SummarizerException if summarization fails after all retries
     * @throws LLMTimeoutException if the LLM call times out
     */
    @Retry(name = "summarizeService", fallbackMethod = "summarizeFallback")
    @TimeLimiter(name = "summarizeService")
    public CompletableFuture<SummarizeResponse> summarize(SummarizeRequest request) {
        // Record incoming request
        metrics.recordRequest();
        metrics.updateInputLength(request.getText().length());

        log.info("Starting summarization for text of length: {}", request.getText().length());

        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Check rate limits FIRST
            rateLimitingService.checkRateLimit();

            // Step 2: Validate input size at service layer
            sizeValidator.validate(request.getText());
            sizeValidator.validateMinimumLength(request.getText());

            // Step 3: Validate character encoding
            encodingValidator.validate(request.getText());

            // Step 4: Sanitize input to prevent prompt injection
            String sanitizedText = InputSanitizer.sanitize(request.getText());
            log.debug("Input sanitization and validation passed");

            // Step 5: Strip HTML tags if present
            String textWithoutHtml = InputSanitizer.stripHtmlTags(sanitizedText);
            if (!textWithoutHtml.equals(sanitizedText)) {
                log.info("HTML tags were stripped from input");
            }

            // Step 6: Determine the summary style
            SummaryStyle style = request.getSummaryStyle() != null
                    ? request.getSummaryStyle()
                    : SummaryStyle.CONCISE;

            // Step 7: Build the prompts
            String systemPrompt = promptService.getSystemPrompt();
            String userPrompt = promptService.buildPrompt(
                    textWithoutHtml,
                    style,
                    request.getMaxLength()
            );
            log.debug("Prompts built successfully");

            // Step 8: Call the LLM
            log.debug("Calling LLM with model: {}", properties.getModel());
            String summary = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            // Step 9: Calculate processing time
            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Summarization completed in {} ms. Summary length: {} characters",
                    processingTime, summary.length());

            // Record metrics
            metrics.recordRequestDuration(processingTime);
            metrics.recordSuccess();

            // Step 10: Validate the summary
            if (summary == null || summary.trim().isEmpty()) {
                metrics.recordFailure();
                throw new SummarizerException("LLM returned an empty summary");
            }

            // Step 11: Build and return the response
            SummarizeResponse response = SummarizeResponse.builder()
                    .summary(summary.trim())
                    .inputLength(request.getText().length())
                    .summaryLength(summary.length())
                    .model(properties.getModel())
                    .processingTimeMs(processingTime)
                    .build();

            return CompletableFuture.completedFuture(response);

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Summarization failed after {} ms", processingTime, e);

            // Record failure metrics
            metrics.recordRequestDuration(processingTime);
            if (e instanceof LLMTimeoutException) {
                metrics.recordTimeout();
            }
            metrics.recordFailure();

            // Re-throw known exceptions without wrapping
            if (e instanceof LLMTimeoutException ||
                e instanceof SummarizerException ||
                e instanceof com.azharkhalid.aitextsummarizer.exception.RateLimitExceededException ||
                e instanceof com.azharkhalid.aitextsummarizer.exception.InvalidInputException) {
                throw e;
            }

            // Wrap unknown exceptions
            throw new SummarizerException("Failed to generate summary: " + e.getMessage(), e);
        }
    }

    /**
     * Fallback method when retry attempts are exhausted.
     * Provides a graceful degradation path.
     *
     * @param request The original request
     * @param exception The exception that triggered the fallback
     * @return CompletableFuture with error response
     */
    private CompletableFuture<SummarizeResponse> summarizeFallback(
            SummarizeRequest request,
            Exception exception) {

        log.error("All retry attempts exhausted for request", exception);

        throw new LLMTimeoutException(
                "Service temporarily unavailable after multiple retry attempts: " +
                exception.getMessage()
        );
    }
}

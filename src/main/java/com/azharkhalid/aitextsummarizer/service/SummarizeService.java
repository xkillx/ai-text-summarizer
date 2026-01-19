package com.azharkhalid.aitextsummarizer.service;

import com.azharkhalid.aitextsummarizer.config.SummarizeProperties;
import com.azharkhalid.aitextsummarizer.dto.request.SummarizeRequest;
import com.azharkhalid.aitextsummarizer.dto.response.SummarizeResponse;
import com.azharkhalid.aitextsummarizer.enums.SummaryStyle;
import com.azharkhalid.aitextsummarizer.exception.LLMTimeoutException;
import com.azharkhalid.aitextsummarizer.exception.SummarizerException;
import com.azharkhalid.aitextsummarizer.util.InputSanitizer;
import com.azharkhalid.aitextsummarizer.validation.CharacterEncodingValidator;
import com.azharkhalid.aitextsummarizer.validation.MaxInputSizeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Core service for handling text summarization using LLM.
 * Enhanced with comprehensive validation and security checks.
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

    /**
     * Summarizes the provided text using the configured LLM.
     *
     * @param request The summarization request containing text and options
     * @return SummarizeResponse containing the summary and metadata
     * @throws SummarizerException if summarization fails
     */
    public SummarizeResponse summarize(SummarizeRequest request) {
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

            // Step 10: Validate the summary
            if (summary == null || summary.trim().isEmpty()) {
                throw new SummarizerException("LLM returned an empty summary");
            }

            // Step 11: Build and return the response
            return SummarizeResponse.builder()
                    .summary(summary.trim())
                    .inputLength(request.getText().length())
                    .summaryLength(summary.length())
                    .model(properties.getModel())
                    .processingTimeMs(processingTime)
                    .build();

        } catch (Exception e) {
            long processingTime = System.currentTimeMillis() - startTime;
            log.error("Summarization failed after {} ms", processingTime, e);

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
}

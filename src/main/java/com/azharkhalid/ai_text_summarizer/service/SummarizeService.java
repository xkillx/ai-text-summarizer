package com.azharkhalid.ai_text_summarizer.service;

import com.azharkhalid.ai_text_summarizer.config.SummarizeProperties;
import com.azharkhalid.ai_text_summarizer.dto.request.SummarizeRequest;
import com.azharkhalid.ai_text_summarizer.dto.response.SummarizeResponse;
import com.azharkhalid.ai_text_summarizer.enums.SummaryStyle;
import com.azharkhalid.ai_text_summarizer.exception.LLMTimeoutException;
import com.azharkhalid.ai_text_summarizer.exception.SummarizerException;
import com.azharkhalid.ai_text_summarizer.util.InputSanitizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * Core service for handling text summarization using LLM.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SummarizeService {

    private final ChatClient chatClient;
    private final PromptService promptService;
    private final SummarizeProperties properties;

    /**
     * Summarizes the provided text using the configured LLM.
     *
     * @param request The summarization request containing text and options
     * @return SummarizeResponse containing the summary and metadata
     * @throws SummarizerException if summarization fails
     * @throws LLMTimeoutException if the LLM call times out
     */
    public SummarizeResponse summarize(SummarizeRequest request) {
        log.info("Starting summarization for text of length: {}", request.getText().length());

        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Sanitize input to prevent prompt injection
            String sanitizedText = InputSanitizer.sanitize(request.getText());
            log.debug("Input sanitized successfully");

            // Step 2: Determine the summary style (default to CONCISE if not specified)
            SummaryStyle style = request.getSummaryStyle() != null
                    ? request.getSummaryStyle()
                    : SummaryStyle.CONCISE;

            // Step 3: Build the prompt using PromptService
            String systemPrompt = promptService.getSystemPrompt();
            String userPrompt = promptService.buildPrompt(
                    sanitizedText,
                    style,
                    request.getMaxLength()
            );
            log.debug("Prompts built. System prompt length: {}, User prompt length: {}",
                    systemPrompt.length(), userPrompt.length());

            // Step 4: Call the LLM
            log.debug("Calling LLM with model: {}", properties.getModel());
            String summary = chatClient.prompt()
                    .system(systemPrompt)
                    .user(userPrompt)
                    .call()
                    .content();

            // Step 5: Calculate processing time
            long processingTime = System.currentTimeMillis() - startTime;
            log.info("Summarization completed in {} ms. Summary length: {} characters",
                    processingTime, summary.length());

            // Step 6: Validate the summary
            if (summary == null || summary.trim().isEmpty()) {
                throw new SummarizerException("LLM returned an empty summary");
            }

            // Step 7: Build and return the response
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

            // Re-throw known exceptions
            if (e instanceof LLMTimeoutException || e instanceof SummarizerException) {
                throw e;
            }

            // Wrap unknown exceptions
            throw new SummarizerException("Failed to generate summary: " + e.getMessage(), e);
        }
    }
}

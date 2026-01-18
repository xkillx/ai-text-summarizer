package com.azharkhalid.aitextsummarizer.service;

import com.azharkhalid.aitextsummarizer.enums.SummaryStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service responsible for constructing prompts for the LLM.
 */
@Slf4j
@Service
public class PromptService {

    /**
     * System prompt that defines the AI's behavior and role.
     * This is kept separate from user input to maintain security.
     */
    private static final String SYSTEM_PROMPT =
        "You are a professional technical writer and editor. " +
        "Your task is to summarize text clearly, accurately, and concisely. " +
        "Follow these guidelines:\n" +
        "1. Preserve the core meaning and key information\n" +
        "2. Remove redundancy and filler content\n" +
        "3. Do not add information not present in the original text\n" +
        "4. Maintain a neutral, professional tone\n" +
        "5. Use clear, straightforward language";

    /**
     * Template for building the user prompt.
     */
    private static final String PROMPT_TEMPLATE =
        "Please summarize the following text using a %s style.%s\n\n" +
        "%s\n\n" +
        "---\n" +
        "%s";

    /**
     * Builds the complete prompt for the LLM based on the request parameters.
     *
     * @param text The text to summarize
     * @param style The desired summary style
     * @param maxLength Optional maximum length in words
     * @return The constructed prompt string
     */
    public String buildPrompt(String text, SummaryStyle style, Integer maxLength) {
        log.debug("Building prompt with style: {}, maxLength: {}", style, maxLength);

        String styleDescription = style.getPromptSuffix();
        String lengthConstraint = buildLengthConstraint(maxLength);

        String prompt = String.format(
            PROMPT_TEMPLATE,
            style.name().toLowerCase(),
            lengthConstraint,
            styleDescription + ". " + lengthConstraint,
            text
        );

        log.debug("Built prompt (length: {} chars)", prompt.length());
        return prompt;
    }

    /**
     * Returns the system prompt that defines the AI's behavior.
     *
     * @return The system prompt string
     */
    public String getSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * Builds a length constraint string for the prompt.
     *
     * @param maxLength The maximum length in words, or null if no constraint
     * @return A string describing the length constraint
     */
    private String buildLengthConstraint(Integer maxLength) {
        if (maxLength == null || maxLength <= 0) {
            return "";
        }
        return String.format(" Limit the summary to approximately %d words.", maxLength);
    }

    /**
     * Validates that the input text meets minimum requirements.
     *
     * @param text The text to validate
     * @return true if valid
     * @throws IllegalArgumentException if text is too short
     */
    public boolean validateInputLength(String text) {
        if (text == null || text.trim().length() < 100) {
            throw new IllegalArgumentException(
                "Input text must be at least 100 characters long"
            );
        }
        return true;
    }
}

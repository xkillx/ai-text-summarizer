package com.azharkhalid.aitextsummarizer.validation;

import com.azharkhalid.aitextsummarizer.config.SummarizeProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validator for enforcing maximum input size constraints.
 * Provides service-layer validation as an additional security layer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MaxInputSizeValidator {

    private final SummarizeProperties properties;

    /**
     * Validates that the input text does not exceed the maximum allowed length.
     *
     * @param text The input text to validate
     * @throws com.azharkhalid.aitextsummarizer.exception.InvalidInputException
     *         if the input exceeds maximum length
     */
    public void validate(String text) {
        if (text == null) {
            throw new com.azharkhalid.aitextsummarizer.exception.InvalidInputException(
                    "Input text cannot be null"
            );
        }

        int maxLength = properties.getMaxInputLength();
        if (text.length() > maxLength) {
            log.warn("Input text exceeds maximum length: {} characters (max: {})",
                    text.length(), maxLength);
            throw new com.azharkhalid.aitextsummarizer.exception.InvalidInputException(
                    String.format("Input text exceeds maximum length of %d characters. " +
                                    "Provided: %d characters",
                            maxLength, text.length())
            );
        }

        log.debug("Input text length validation passed: {} characters", text.length());
    }

    /**
     * Validates that the input text meets the minimum length requirement.
     *
     * @param text The input text to validate
     * @throws com.azharkhalid.aitextsummarizer.exception.InvalidInputException
     *         if the input is below minimum length
     */
    public void validateMinimumLength(String text) {
        if (text == null || text.trim().length() < 100) {
            log.warn("Input text below minimum length: {} characters",
                    text != null ? text.length() : 0);
            throw new com.azharkhalid.aitextsummarizer.exception.InvalidInputException(
                    "Input text must be at least 100 characters long for meaningful summarization"
            );
        }
    }
}

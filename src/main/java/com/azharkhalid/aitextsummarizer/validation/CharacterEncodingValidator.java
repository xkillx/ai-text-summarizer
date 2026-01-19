package com.azharkhalid.aitextsummarizer.validation;

import com.azharkhalid.aitextsummarizer.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Validator for ensuring proper character encoding in input text.
 * Prevents encoding issues and potential injection attacks via special characters.
 */
@Slf4j
@Component
public class CharacterEncodingValidator {

    /**
     * Pattern to detect potentially dangerous control characters.
     * Allows common whitespace and newline characters but rejects others.
     */
    private static final Pattern CONTROL_CHARACTER_PATTERN = Pattern.compile(
            "[\\x00-\\x08\\x0B-\\x0C\\x0E-\\x1F\\x7F]"
    );

    /**
     * Charset encoder for validation.
     */
    private final CharsetEncoder utf8Encoder = StandardCharsets.UTF_8.newEncoder();

    /**
     * Validates that the input text contains only valid UTF-8 characters.
     *
     * @param text The input text to validate
     * @throws InvalidInputException if encoding is invalid
     */
    public void validateUtf8Encoding(String text) {
        if (text == null) {
            throw new InvalidInputException("Input text cannot be null");
        }

        try {
            if (!utf8Encoder.canEncode(text)) {
                log.warn("Input text contains invalid UTF-8 characters");
                throw new InvalidInputException(
                        "Input text contains invalid UTF-8 characters"
                );
            }
        } catch (Exception e) {
            log.error("Error validating UTF-8 encoding", e);
            throw new InvalidInputException(
                    "Error validating character encoding"
            );
        }

        log.debug("UTF-8 encoding validation passed");
    }

    /**
     * Validates that the input doesn't contain dangerous control characters.
     *
     * @param text The input text to validate
     * @throws InvalidInputException if dangerous characters are found
     */
    public void validateNoControlCharacters(String text) {
        if (text == null) {
            return;
        }

        if (CONTROL_CHARACTER_PATTERN.matcher(text).find()) {
            log.warn("Input text contains dangerous control characters");
            throw new InvalidInputException(
                    "Input text contains invalid control characters"
            );
        }

        log.debug("Control character validation passed");
    }

    /**
     * Performs comprehensive character validation.
     *
     * @param text The input text to validate
     * @throws InvalidInputException if any validation fails
     */
    public void validate(String text) {
        validateUtf8Encoding(text);
        validateNoControlCharacters(text);
    }
}

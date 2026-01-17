package com.azharkhalid.ai_text_summarizer.util;

import com.azharkhalid.ai_text_summarizer.exception.InvalidInputException;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Pattern;

/**
 * Utility class for sanitizing user input to prevent prompt injection attacks.
 */
@Slf4j
public class InputSanitizer {

    /**
     * Pattern to detect potentially dangerous keywords that might indicate prompt injection attempts.
     * Matches words like: ignore, override, system, admin, instructions, etc.
     */
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
        "(?i)(\\bignore\\s+(all\\s+)?(previous|above)?\\s+(instructions|prompts?)\\b|" +
        "\\boverride\\b|" +
        "\\bsystem\\s*:\\s*instruction|" +
        "\\badmin\\s+(mode|privilege)\\b|" +
        "\\bforget\\s+(everything|all\\s+instructions)\\b|" +
        "\\bnew\\s+role\\b|" +
        "\\bjailbreak\\b)",
        Pattern.CASE_INSENSITIVE
    );

    /**
     * Maximum allowed input length as a safety measure.
     */
    private static final int MAX_INPUT_LENGTH = 10000;

    /**
     * Private constructor to prevent instantiation.
     */
    private InputSanitizer() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Sanitizes the input text by checking for suspicious patterns and enforcing size limits.
     *
     * @param input The input text to sanitize
     * @return The sanitized (trimmed) input
     * @throws InvalidInputException if dangerous patterns are detected or input is too large
     */
    public static String sanitize(String input) {
        if (input == null) {
            throw new InvalidInputException("Input text cannot be null");
        }

        String trimmed = input.trim();

        // Check maximum length
        if (trimmed.length() > MAX_INPUT_LENGTH) {
            log.warn("Input exceeded maximum length: {} characters", trimmed.length());
            throw new InvalidInputException(
                String.format("Input text exceeds maximum length of %d characters", MAX_INPUT_LENGTH)
            );
        }

        // Check for dangerous patterns
        if (DANGEROUS_PATTERN.matcher(trimmed).find()) {
            log.warn("Potentially dangerous input pattern detected");
            throw new InvalidInputException(
                "Input contains suspicious content that may indicate an attempt to manipulate the system"
            );
        }

        return trimmed;
    }

    /**
     * Removes HTML tags from input text.
     *
     * @param input The input text that may contain HTML
     * @return Text with HTML tags removed
     */
    public static String stripHtmlTags(String input) {
        if (input == null) {
            return null;
        }
        return input.replaceAll("<[^>]*>", "");
    }

    /**
     * Validates that the input contains only printable ASCII characters.
     *
     * @param input The input text to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPrintableAscii(String input) {
        if (input == null) {
            return false;
        }
        return input.matches("\\p{Print}+");
    }
}

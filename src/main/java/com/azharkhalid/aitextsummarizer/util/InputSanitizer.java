package com.azharkhalid.aitextsummarizer.util;

import com.azharkhalid.aitextsummarizer.exception.InvalidInputException;
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

    /**
     * Removes potentially dangerous markdown/JSON that could be used for injection.
     *
     * @param input The input text to sanitize
     * @return Text with dangerous patterns neutralized
     */
    public static String sanitizeMarkdownJson(String input) {
        if (input == null) {
            return null;
        }

        // Escape JSON control characters
        String sanitized = input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");

        return sanitized;
    }

    /**
     * Normalizes whitespace in the input text.
     * Helps prevent attacks using excessive whitespace or zero-width characters.
     *
     * @param input The input text to normalize
     * @return Text with normalized whitespace
     */
    public static String normalizeWhitespace(String input) {
        if (input == null) {
            return null;
        }

        // Replace multiple whitespace characters with a single space
        // Also handles zero-width spaces and other unusual whitespace
        return input.replaceAll("\\s+", " ").trim();
    }

    /**
     * Validates that the input doesn't contain zero-width characters
     * that can be used in attacks.
     *
     * @param input The input text to validate
     * @throws InvalidInputException if zero-width characters are detected
     */
    public static void validateNoZeroWidthCharacters(String input) {
        if (input == null) {
            throw new InvalidInputException("Input cannot be null");
        }

        // Check for zero-width characters
        if (input.contains("\u200B") ||  // Zero Width Space
            input.contains("\u200C") ||  // Zero Width Non-Joiner
            input.contains("\u200D") ||  // Zero Width Joiner
            input.contains("\uFEFF")) {  // Zero Width No-Break Space

            throw new InvalidInputException(
                    "Input contains invalid characters (zero-width characters)"
            );
        }
    }

    /**
     * Comprehensive sanitization that applies all security measures.
     *
     * @param input The input text to sanitize
     * @return Fully sanitized text
     * @throws InvalidInputException if validation fails
     */
    public static String sanitizeComprehensive(String input) {
        // First, validate
        validateNoZeroWidthCharacters(input);

        // Then sanitize
        String sanitized = sanitize(input);

        // Strip HTML
        sanitized = stripHtmlTags(sanitized);

        // Normalize whitespace
        sanitized = normalizeWhitespace(sanitized);

        return sanitized;
    }
}

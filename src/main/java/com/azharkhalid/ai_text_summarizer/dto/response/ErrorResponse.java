package com.azharkhalid.ai_text_summarizer.dto.response;

import java.time.LocalDateTime;

/**
 * Standard error response format for all API errors.
 */
public record ErrorResponse(
    /**
     * Error code for programmatic handling.
     */
    String errorCode,

    /**
     * Human-readable error message.
     */
    String message,

    /**
     * Timestamp when the error occurred.
     */
    LocalDateTime timestamp
) {
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, LocalDateTime.now());
    }
}

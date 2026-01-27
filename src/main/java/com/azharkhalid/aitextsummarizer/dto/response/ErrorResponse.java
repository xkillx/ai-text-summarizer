package com.azharkhalid.aitextsummarizer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Standard error response format for all API errors.
 */
@Schema(description = "Error response returned when an exception occurs")
public record ErrorResponse(
    /**
     * Error code for programmatic handling.
     */
    @Schema(
            description = "Unique error code for programmatic handling",
            example = "INVALID_INPUT"
    )
    String errorCode,

    /**
     * Human-readable error message.
     */
    @Schema(
            description = "Human-readable error message",
            example = "Input text must be at least 100 characters"
    )
    String message,

    /**
     * Timestamp when the error occurred.
     */
    @Schema(
            description = "ISO 8601 timestamp when the error occurred",
            example = "2025-01-27T10:30:00"
    )
    LocalDateTime timestamp
) {
    public ErrorResponse(String errorCode, String message) {
        this(errorCode, message, LocalDateTime.now());
    }
}

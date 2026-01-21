package com.azharkhalid.aitextsummarizer.controller;

import com.azharkhalid.aitextsummarizer.dto.response.ErrorResponse;
import com.azharkhalid.aitextsummarizer.exception.InvalidInputException;
import com.azharkhalid.aitextsummarizer.exception.LLMTimeoutException;
import com.azharkhalid.aitextsummarizer.exception.RateLimitExceededException;
import com.azharkhalid.aitextsummarizer.exception.SummarizerException;
import com.azharkhalid.aitextsummarizer.logging.StructuredLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Global exception handler for all REST controllers.
 * Provides consistent error response format across the API with structured logging.
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final StructuredLogger structuredLogger;

    /**
     * Handles validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = errors.isEmpty()
                ? "Validation failed"
                : "Validation failed: " + errors.toString();

        structuredLogger.logValidationError(requestId, "multiple", errors.toString());
        log.warn("Validation error for request {}: {}", requestId, message);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", message, LocalDateTime.now()));
    }

    /**
     * Handles InvalidInputException.
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(
            InvalidInputException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        structuredLogger.logValidationError(requestId, "input", ex.getMessage());
        log.warn("Invalid input for request {}: {}", requestId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("INVALID_INPUT", ex.getMessage(), LocalDateTime.now()));
    }

    /**
     * Handles RateLimitExceededException.
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        structuredLogger.logRateLimitExceeded(requestId);
        log.warn("Rate limit exceeded for request {}: {}", requestId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(new ErrorResponse("RATE_LIMIT_EXCEEDED", ex.getMessage(), LocalDateTime.now()));
    }

    /**
     * Handles LLMTimeoutException.
     */
    @ExceptionHandler(LLMTimeoutException.class)
    public ResponseEntity<ErrorResponse> handleLLMTimeout(
            LLMTimeoutException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        structuredLogger.logSummarizeFailure(requestId, "TIMEOUT", ex.getMessage());
        log.error("LLM timeout for request {}: {}", requestId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ErrorResponse("LLM_TIMEOUT",
                        "The summarization service is temporarily unavailable. Please try again later.",
                        LocalDateTime.now()));
    }

    /**
     * Handles SummarizerException (base exception).
     */
    @ExceptionHandler(SummarizerException.class)
    public ResponseEntity<ErrorResponse> handleSummarizerException(
            SummarizerException ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        structuredLogger.logSummarizeFailure(requestId, "SUMMARIZER_ERROR", ex.getMessage());
        log.error("Summarizer exception for request {}: {}", requestId, ex.getMessage());

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("SUMMARIZER_ERROR", ex.getMessage(), LocalDateTime.now()));
    }

    /**
     * Handles all other exceptions not specifically handled above.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request
    ) {
        String requestId = generateRequestId();
        structuredLogger.logSummarizeFailure(requestId, "INTERNAL_ERROR", ex.getMessage());
        log.error("Unexpected error for request {}", requestId, ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now()));
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}

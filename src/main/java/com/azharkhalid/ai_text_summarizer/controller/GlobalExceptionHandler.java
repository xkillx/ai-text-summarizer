package com.azharkhalid.ai_text_summarizer.controller;

import com.azharkhalid.ai_text_summarizer.dto.response.ErrorResponse;
import com.azharkhalid.ai_text_summarizer.exception.InvalidInputException;
import com.azharkhalid.ai_text_summarizer.exception.LLMTimeoutException;
import com.azharkhalid.ai_text_summarizer.exception.RateLimitExceededException;
import com.azharkhalid.ai_text_summarizer.exception.SummarizerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 * Provides consistent error response format across the API.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String message = errors.isEmpty()
                ? "Validation failed"
                : "Validation failed: " + errors.toString();

        log.warn("Validation error: {}", message);

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
            WebRequest request
    ) {
        log.warn("Invalid input: {}", ex.getMessage());

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
            WebRequest request
    ) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());

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
            WebRequest request
    ) {
        log.error("LLM timeout: {}", ex.getMessage());

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
            WebRequest request
    ) {
        log.error("Summarizer exception: {}", ex.getMessage());

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
            WebRequest request
    ) {
        log.error("Unexpected error occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("INTERNAL_ERROR",
                        "An unexpected error occurred. Please try again later.",
                        LocalDateTime.now()));
    }
}

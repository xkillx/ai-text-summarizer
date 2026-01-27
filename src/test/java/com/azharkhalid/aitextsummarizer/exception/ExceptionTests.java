package com.azharkhalid.aitextsummarizer.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Exception Tests")
class ExceptionTests {

    @Test
    @DisplayName("SummarizerException should have message")
    void summarizerExceptionShouldHaveMessage() {
        SummarizerException exception = new SummarizerException("Test error");

        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("SummarizerException should have message and cause")
    void summarizerExceptionShouldHaveMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        SummarizerException exception = new SummarizerException("Test error", cause);

        assertThat(exception.getMessage()).isEqualTo("Test error");
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("LLMTimeoutException should have message")
    void llmTimeoutExceptionShouldHaveMessage() {
        LLMTimeoutException exception = new LLMTimeoutException("Timeout occurred");

        assertThat(exception.getMessage()).isEqualTo("Timeout occurred");
        assertThat(exception).isInstanceOf(SummarizerException.class);
    }

    @Test
    @DisplayName("InvalidInputException should have message")
    void invalidInputExceptionShouldHaveMessage() {
        InvalidInputException exception = new InvalidInputException("Invalid input");

        assertThat(exception.getMessage()).isEqualTo("Invalid input");
        assertThat(exception).isInstanceOf(SummarizerException.class);
    }

    @Test
    @DisplayName("RateLimitExceededException should have message")
    void rateLimitExceededExceptionShouldHaveMessage() {
        RateLimitExceededException exception = new RateLimitExceededException("Rate limit exceeded");

        assertThat(exception.getMessage()).isEqualTo("Rate limit exceeded");
        assertThat(exception).isInstanceOf(SummarizerException.class);
    }
}

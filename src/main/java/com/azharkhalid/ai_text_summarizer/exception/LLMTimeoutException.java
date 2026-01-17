package com.azharkhalid.ai_text_summarizer.exception;

/**
 * Thrown when the LLM provider times out or is unavailable.
 */
public class LLMTimeoutException extends SummarizerException {

    public LLMTimeoutException(String message) {
        super(message);
    }

    public LLMTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}

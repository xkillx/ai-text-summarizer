package com.azharkhalid.ai_text_summarizer.exception;

/**
 * Thrown when the rate limit for the API has been exceeded.
 */
public class RateLimitExceededException extends SummarizerException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}

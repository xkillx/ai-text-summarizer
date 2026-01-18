package com.azharkhalid.aitextsummarizer.exception;

/**
 * Thrown when the rate limit for the API has been exceeded.
 */
public class RateLimitExceededException extends SummarizerException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}

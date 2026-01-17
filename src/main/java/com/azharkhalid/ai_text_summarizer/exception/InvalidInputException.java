package com.azharkhalid.ai_text_summarizer.exception;

/**
 * Thrown when input validation fails or suspicious content is detected.
 */
public class InvalidInputException extends SummarizerException {

    public InvalidInputException(String message) {
        super(message);
    }

    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}

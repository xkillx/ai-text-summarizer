package com.azharkhalid.ai_text_summarizer.exception;

public class SummarizerException extends RuntimeException {
    public SummarizerException(String message) {
        super(message);
    }

    public SummarizerException(String message, Throwable cause) {
        super(message, cause);
    }
}

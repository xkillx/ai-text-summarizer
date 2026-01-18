package com.azharkhalid.aitextsummarizer.enums;

import lombok.Getter;

/**
 * Defines the different styles of summarization supported by the API.
 */
@Getter
public enum SummaryStyle {

    /**
     * Brief, direct summary without unnecessary elaboration.
     */
    CONCISE("Provide a brief, direct summary focusing on key points"),

    /**
     * Bulleted list format for easy scanning.
     */
    BULLET("Provide a bulleted list summary with clear, concise points"),

    /**
     * Executive-level summary focusing on strategic insights and implications.
     */
    EXECUTIVE("Provide an executive-level summary focusing on key insights, strategic implications, and business impact");

    private final String promptSuffix;

    SummaryStyle(String promptSuffix) {
        this.promptSuffix = promptSuffix;
    }
}

package com.azharkhalid.aitextsummarizer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response body for successful summarization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SummarizeResponse {

    /**
     * The generated summary text.
     */
    private String summary;

    /**
     * Length of the input text in characters.
     */
    private int inputLength;

    /**
     * Length of the summary in characters.
     */
    private int summaryLength;

    /**
     * The model used for generation (e.g., "gpt-4o-mini").
     */
    private String model;

    /**
     * Time taken to process the request in milliseconds.
     */
    private long processingTimeMs;
}

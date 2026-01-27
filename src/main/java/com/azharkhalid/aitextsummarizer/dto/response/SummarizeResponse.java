package com.azharkhalid.aitextsummarizer.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Response object containing the generated summary and metadata")
public class SummarizeResponse {

    /**
     * The generated summary text.
     */
    @Schema(
            description = "The AI-generated summary text",
            example = "AI is machine intelligence, contrasted with natural intelligence in humans and animals. It studies intelligent agents that perceive and act to achieve goals."
    )
    private String summary;

    /**
     * Length of the input text in characters.
     */
    @Schema(
            description = "Length of the original input text in characters",
            example = "523"
    )
    private int inputLength;

    /**
     * Length of the summary in characters.
     */
    @Schema(
            description = "Length of the generated summary in characters",
            example = "167"
    )
    private int summaryLength;

    /**
     * The model used for generation (e.g., "gpt-4o-mini").
     */
    @Schema(
            description = "The AI model used for summarization",
            example = "gpt-4o-mini"
    )
    private String model;

    /**
     * Time taken to process the request in milliseconds.
     */
    @Schema(
            description = "Time taken to process the request in milliseconds",
            example = "856"
    )
    private long processingTimeMs;
}

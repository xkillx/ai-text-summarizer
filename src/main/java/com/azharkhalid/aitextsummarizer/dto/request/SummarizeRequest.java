package com.azharkhalid.aitextsummarizer.dto.request;

import com.azharkhalid.aitextsummarizer.enums.SummaryStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request body for the summarize endpoint.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for text summarization")
public class SummarizeRequest {

    /**
     * The input text to be summarized.
     * Must be at least 100 characters to ensure meaningful content.
     */
    @Schema(
            description = "The text to be summarized. Must be at least 100 characters and will be sanitized for security.",
            example = "Artificial intelligence (AI) is intelligence demonstrated by machines, as opposed to the natural intelligence displayed by humans or animals. Leading AI textbooks define the field as the study of intelligent agents: any system that perceives its environment and takes actions that maximize its chance of achieving its goals.",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 100
    )
    @NotBlank(message = "Text cannot be empty or null")
    @Size(min = 100, max = 10000, message = "Text must be between 100 and 10000 characters")
    private String text;

    /**
     * Maximum length of the summary in words.
     * Optional - if not specified, the model will determine appropriate length.
     */
    @Schema(
            description = "Maximum length of the summary in characters. If null, uses model default.",
            example = "150",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            minimum = "50",
            maximum = "500"
    )
    @Min(value = 50, message = "maxLength must be at least 50 words")
    @Max(value = 1000, message = "maxLength cannot exceed 1000 words")
    private Integer maxLength;

    /**
     * The style of summary to generate.
     * Optional - defaults to CONCISE if not specified.
     */
    @Schema(
            description = "Style of the summary. Defaults to CONCISE if not specified.",
            example = "CONCISE",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            allowableValues = {"CONCISE", "BULLET", "EXECUTIVE"}
    )
    private SummaryStyle summaryStyle;
}

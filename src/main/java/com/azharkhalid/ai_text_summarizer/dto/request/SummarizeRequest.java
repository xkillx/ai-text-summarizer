package com.azharkhalid.ai_text_summarizer.dto.request;

import com.azharkhalid.ai_text_summarizer.enums.SummaryStyle;
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
public class SummarizeRequest {

    /**
     * The input text to be summarized.
     * Must be at least 100 characters to ensure meaningful content.
     */
    @NotBlank(message = "Text cannot be empty or null")
    @Size(min = 100, max = 10000, message = "Text must be between 100 and 10000 characters")
    private String text;

    /**
     * Maximum length of the summary in words.
     * Optional - if not specified, the model will determine appropriate length.
     */
    @Min(value = 50, message = "maxLength must be at least 50 words")
    @Max(value = 1000, message = "maxLength cannot exceed 1000 words")
    private Integer maxLength;

    /**
     * The style of summary to generate.
     * Optional - defaults to CONCISE if not specified.
     */
    private SummaryStyle summaryStyle;
}

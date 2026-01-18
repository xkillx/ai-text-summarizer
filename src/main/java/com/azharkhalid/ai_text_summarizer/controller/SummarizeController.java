package com.azharkhalid.ai_text_summarizer.controller;

import com.azharkhalid.ai_text_summarizer.dto.request.SummarizeRequest;
import com.azharkhalid.ai_text_summarizer.dto.response.SummarizeResponse;
import com.azharkhalid.ai_text_summarizer.service.SummarizeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for text summarization endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Summarization", description = "Text summarization API endpoints")
public class SummarizeController {

    private final SummarizeService summarizeService;

    /**
     * Summarizes the provided text using AI/LLM.
     *
     * @param request The summarization request
     * @return SummarizeResponse containing the summary and metadata
     */
    @PostMapping(
            value = "/summarize",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Summarize text",
            description = """
                    Generates a concise summary of the provided text using an AI language model.
                    The text must be between 100 and 10,000 characters. Supports multiple summary styles
                    (concise, bullet, executive) and optional maximum length constraints.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully generated summary",
                    content = @Content(schema = @Schema(implementation = SummarizeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input (e.g., text too short, too long, or contains suspicious content)"
            ),
            @ApiResponse(
                    responseCode = "429",
                    description = "Rate limit exceeded"
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Service temporarily unavailable (LLM provider timeout)"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    public ResponseEntity<SummarizeResponse> summarize(
            @Valid @RequestBody SummarizeRequest request
    ) {
        log.info("Received summarization request. Text length: {}, Style: {}, MaxLength: {}",
                request.getText().length(),
                request.getSummaryStyle(),
                request.getMaxLength()
        );

        SummarizeResponse response = summarizeService.summarize(request);

        log.info("Returning summary. Summary length: {}, Processing time: {} ms",
                response.getSummaryLength(),
                response.getProcessingTimeMs()
        );

        return ResponseEntity.ok(response);
    }
}

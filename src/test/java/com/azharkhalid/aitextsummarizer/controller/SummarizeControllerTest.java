package com.azharkhalid.aitextsummarizer.controller;

import com.azharkhalid.aitextsummarizer.dto.request.SummarizeRequest;
import com.azharkhalid.aitextsummarizer.dto.response.SummarizeResponse;
import com.azharkhalid.aitextsummarizer.enums.SummaryStyle;
import com.azharkhalid.aitextsummarizer.exception.InvalidInputException;
import com.azharkhalid.aitextsummarizer.exception.RateLimitExceededException;
import com.azharkhalid.aitextsummarizer.service.SummarizeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("SummarizeController Unit Tests")
@WebMvcTest(SummarizeController.class)
class SummarizeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SummarizeService summarizeService;

    @Test
    @DisplayName("Should return 200 and summary when request is valid")
    void shouldReturn200WhenRequestIsValid() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                150,
                SummaryStyle.CONCISE
        );

        SummarizeResponse response = SummarizeResponse.builder()
                .summary("This is a summary.")
                .inputLength(request.getText().length())
                .summaryLength(18)
                .model("gpt-4o-mini")
                .processingTimeMs(500)
                .build();

        when(summarizeService.summarize(any(SummarizeRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("This is a summary."))
                .andExpect(jsonPath("$.inputLength").value(request.getText().length()))
                .andExpect(jsonPath("$.summaryLength").value(18))
                .andExpect(jsonPath("$.model").value("gpt-4o-mini"))
                .andExpect(jsonPath("$.processingTimeMs").value(500));

        verify(summarizeService, times(1)).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when text is too short")
    void shouldReturn400WhenTextIsTooShort() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "Too short",
                150,
                SummaryStyle.CONCISE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(summarizeService, never()).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when text is blank")
    void shouldReturn400WhenTextIsBlank() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "",
                150,
                SummaryStyle.CONCISE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(summarizeService, never()).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when maxLength is negative")
    void shouldReturn400WhenMaxLengthIsNegative() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                -10,
                SummaryStyle.CONCISE
        );

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(summarizeService, never()).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when request body is malformed")
    void shouldReturn400WhenRequestBodyIsMalformed() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isBadRequest());

        verify(summarizeService, never()).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should use default style when summaryStyle is null")
    void shouldUseDefaultStyleWhenSummaryStyleIsNull() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                150,
                null
        );

        SummarizeResponse response = SummarizeResponse.builder()
                .summary("This is a summary.")
                .inputLength(request.getText().length())
                .summaryLength(18)
                .model("gpt-4o-mini")
                .processingTimeMs(500)
                .build();

        when(summarizeService.summarize(any(SummarizeRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(summarizeService, times(1)).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should handle BULLET style correctly")
    void shouldHandleBulletStyleCorrectly() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                150,
                SummaryStyle.BULLET
        );

        SummarizeResponse response = SummarizeResponse.builder()
                .summary("- Point 1\n- Point 2\n- Point 3")
                .inputLength(request.getText().length())
                .summaryLength(30)
                .model("gpt-4o-mini")
                .processingTimeMs(600)
                .build();

        when(summarizeService.summarize(any(SummarizeRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("- Point 1\n- Point 2\n- Point 3"));

        verify(summarizeService, times(1)).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should handle EXECUTIVE style correctly")
    void shouldHandleExecutiveStyleCorrectly() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                150,
                SummaryStyle.EXECUTIVE
        );

        SummarizeResponse response = SummarizeResponse.builder()
                .summary("Executive Summary: Key insights and findings.")
                .inputLength(request.getText().length())
                .summaryLength(45)
                .model("gpt-4o-mini")
                .processingTimeMs(700)
                .build();

        when(summarizeService.summarize(any(SummarizeRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(response));

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value("Executive Summary: Key insights and findings."));

        verify(summarizeService, times(1)).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should return 429 when rate limit is exceeded")
    void shouldReturn429WhenRateLimitExceeded() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                150,
                SummaryStyle.CONCISE
        );

        when(summarizeService.summarize(any(SummarizeRequest.class)))
                .thenThrow(new RateLimitExceededException("Rate limit exceeded. Please try again later."));

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());

        verify(summarizeService, times(1)).summarize(any(SummarizeRequest.class));
    }

    @Test
    @DisplayName("Should return 400 when InvalidInputException is thrown")
    void shouldReturn400WhenInvalidInputExceptionThrown() throws Exception {
        // Arrange
        SummarizeRequest request = new SummarizeRequest(
                "This is a test text that is long enough to pass validation. " +
                "It contains more than one hundred characters to ensure it meets " +
                "the minimum length requirement.",
                150,
                SummaryStyle.CONCISE
        );

        when(summarizeService.summarize(any(SummarizeRequest.class)))
                .thenThrow(new InvalidInputException("Input contains suspicious content"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(summarizeService, times(1)).summarize(any(SummarizeRequest.class));
    }
}

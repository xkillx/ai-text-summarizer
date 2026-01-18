package com.azharkhalid.ai_text_summarizer.controller;

import com.azharkhalid.ai_text_summarizer.dto.request.SummarizeRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for SummarizeController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("SummarizeController Integration Tests")
class SummarizeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should return 400 when text is too short")
    void shouldReturn400WhenTextIsTooShort() throws Exception {
        SummarizeRequest request = SummarizeRequest.builder()
                .text("This text is way too short to be summarized.")
                .summaryStyle(com.azharkhalid.ai_text_summarizer.enums.SummaryStyle.CONCISE)
                .build();

        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should return 400 when text is null")
    void shouldReturn400WhenTextIsNull() throws Exception {
        SummarizeRequest request = SummarizeRequest.builder()
                .summaryStyle(com.azharkhalid.ai_text_summarizer.enums.SummaryStyle.CONCISE)
                .build();

        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should return 400 when text exceeds max length")
    void shouldReturn400WhenTextExceedsMaxLength() throws Exception {
        String longText = "a".repeat(10001);

        SummarizeRequest request = SummarizeRequest.builder()
                .text(longText)
                .summaryStyle(com.azharkhalid.ai_text_summarizer.enums.SummaryStyle.CONCISE)
                .build();

        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should return 400 when maxLength is less than minimum")
    void shouldReturn400WhenMaxLengthIsLessThanMinimum() throws Exception {
        SummarizeRequest request = SummarizeRequest.builder()
                .text("This is a valid text that is definitely over one hundred characters in length. " +
                      "It contains multiple sentences and provides enough content for the summarizer to process.")
                .maxLength(10)
                .summaryStyle(com.azharkhalid.ai_text_summarizer.enums.SummaryStyle.CONCISE)
                .build();

        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }

    @Test
    @DisplayName("Should return 400 when maxLength exceeds maximum")
    void shouldReturn400WhenMaxLengthExceedsMaximum() throws Exception {
        SummarizeRequest request = SummarizeRequest.builder()
                .text("This is a valid text that is definitely over one hundred characters in length. " +
                      "It contains multiple sentences and provides enough content for the summarizer to process.")
                .maxLength(2000)
                .summaryStyle(com.azharkhalid.ai_text_summarizer.enums.SummaryStyle.CONCISE)
                .build();

        mockMvc.perform(post("/api/v1/summarize")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
    }
}

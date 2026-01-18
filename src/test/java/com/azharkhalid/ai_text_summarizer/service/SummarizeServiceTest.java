package com.azharkhalid.ai_text_summarizer.service;

import com.azharkhalid.ai_text_summarizer.config.SummarizeProperties;
import com.azharkhalid.ai_text_summarizer.dto.request.SummarizeRequest;
import com.azharkhalid.ai_text_summarizer.dto.response.SummarizeResponse;
import com.azharkhalid.ai_text_summarizer.enums.SummaryStyle;
import com.azharkhalid.ai_text_summarizer.exception.SummarizerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.ChatClientRequestSpec;
import org.springframework.ai.chat.client.ChatClient.CallResponseSpec;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SummarizeService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SummarizeService Tests")
class SummarizeServiceTest {

    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private PromptService promptService;

    @Mock
    private SummarizeProperties properties;

    @InjectMocks
    private SummarizeService summarizeService;

    private SummarizeRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = SummarizeRequest.builder()
                .text("This is a valid text that is definitely over one hundred characters in length. " +
                      "It contains multiple sentences and provides enough content for the summarizer to process.")
                .summaryStyle(SummaryStyle.CONCISE)
                .maxLength(50)
                .build();

        // Setup common mock behavior
        lenient().when(properties.getModel()).thenReturn("gpt-4o-mini");
        lenient().when(promptService.getSystemPrompt()).thenReturn("System prompt");
        lenient().when(promptService.buildPrompt(any(), any(), any()))
                .thenReturn("User prompt");
    }

    /**
     * Helper method to set up the ChatClient mock chain.
     * Using deep stubs to handle the fluent API.
     * Chain: prompt() -> .system() -> .user() -> .call() -> .content()
     */
    private void mockChatClientResponse(String summaryContent) {
        // With deep stubs, we can directly chain the calls
        when(chatClient.prompt().system(any(String.class)).user(any(String.class)).call().content())
                .thenReturn(summaryContent);
    }

    @Test
    @DisplayName("Should return summary when LLM call succeeds")
    void shouldReturnSummaryWhenLLMCallSucceeds() {
        // Arrange
        String expectedSummary = "This is a concise summary of the text.";
        mockChatClientResponse(expectedSummary);

        // Act
        SummarizeResponse response = summarizeService.summarize(validRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getSummary()).isEqualTo(expectedSummary);
        assertThat(response.getInputLength()).isEqualTo(validRequest.getText().length());
        assertThat(response.getSummaryLength()).isEqualTo(expectedSummary.length());
        assertThat(response.getModel()).isEqualTo("gpt-4o-mini");
        assertThat(response.getProcessingTimeMs()).isGreaterThan(0);

        // Verify interactions (atLeastOnce because mock setup also calls prompt())
        verify(chatClient, atLeastOnce()).prompt();
        verify(promptService).getSystemPrompt();
        verify(promptService).buildPrompt(any(), eq(SummaryStyle.CONCISE), eq(50));
    }

    @Test
    @DisplayName("Should use CONCISE style when style is null")
    void shouldUseConciseStyleWhenStyleIsNull() {
        // Arrange
        SummarizeRequest requestWithoutStyle = SummarizeRequest.builder()
                .text(validRequest.getText())
                .summaryStyle(null)
                .build();
        mockChatClientResponse("Summary");

        // Act
        summarizeService.summarize(requestWithoutStyle);

        // Assert
        verify(promptService).buildPrompt(any(), eq(SummaryStyle.CONCISE), isNull());
    }

    @Test
    @DisplayName("Should throw exception when LLM returns empty summary")
    void shouldThrowExceptionWhenLLMReturnsEmptySummary() {
        // Arrange
        mockChatClientResponse("");

        // Act & Assert
        assertThatThrownBy(() -> summarizeService.summarize(validRequest))
                .isInstanceOf(SummarizerException.class)
                .hasMessageContaining("empty summary");
    }

    @Test
    @DisplayName("Should throw exception when LLM returns null summary")
    void shouldThrowExceptionWhenLLMReturnsNullSummary() {
        // Arrange
        mockChatClientResponse(null);

        // Act & Assert
        assertThatThrownBy(() -> summarizeService.summarize(validRequest))
                .isInstanceOf(SummarizerException.class)
                .hasMessageContaining("Failed to generate summary");
    }

    @Test
    @DisplayName("Should call prompt service with correct parameters")
    void shouldCallPromptServiceWithCorrectParameters() {
        // Arrange
        mockChatClientResponse("Summary text");

        // Act
        summarizeService.summarize(validRequest);

        // Assert
        verify(promptService).getSystemPrompt();
        verify(promptService).buildPrompt(
                argThat(text -> text.length() > 100),
                eq(SummaryStyle.CONCISE),
                eq(50)
        );
    }
}

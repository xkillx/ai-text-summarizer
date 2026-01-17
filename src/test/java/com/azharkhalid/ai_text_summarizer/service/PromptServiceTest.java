package com.azharkhalid.ai_text_summarizer.service;

import com.azharkhalid.ai_text_summarizer.enums.SummaryStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("PromptService Tests")
class PromptServiceTest {

    private PromptService promptService;

    @BeforeEach
    void setUp() {
        promptService = new PromptService();
    }

    @Test
    @DisplayName("Should build prompt with concise style")
    void shouldBuildPromptWithConciseStyle() {
        String text = "This is a test text that needs to be summarized. It contains multiple sentences.";

        String prompt = promptService.buildPrompt(text, SummaryStyle.CONCISE, null);

        assertThat(prompt).contains("concise");
        assertThat(prompt).contains(text);
        assertThat(prompt).contains("brief, direct summary");
    }

    @Test
    @DisplayName("Should build prompt with bullet style")
    void shouldBuildPromptWithBulletStyle() {
        String text = "Test text for bullet summarization.";

        String prompt = promptService.buildPrompt(text, SummaryStyle.BULLET, null);

        assertThat(prompt).contains("bullet");
        assertThat(prompt).contains("bulleted list");
    }

    @Test
    @DisplayName("Should build prompt with executive style")
    void shouldBuildPromptWithExecutiveStyle() {
        String text = "Strategic business text for executive summary.";

        String prompt = promptService.buildPrompt(text, SummaryStyle.EXECUTIVE, null);

        assertThat(prompt).contains("executive");
        assertThat(prompt).contains("executive-level");
    }

    @Test
    @DisplayName("Should include length constraint when maxLength is provided")
    void shouldIncludeLengthConstraintWhenMaxLengthProvided() {
        String text = "Test text";
        int maxLength = 150;

        String prompt = promptService.buildPrompt(text, SummaryStyle.CONCISE, maxLength);

        assertThat(prompt).contains("150");
        assertThat(prompt).contains("words");
    }

    @Test
    @DisplayName("Should not include length constraint when maxLength is null")
    void shouldNotIncludeLengthConstraintWhenMaxLengthIsNull() {
        String text = "Test text";

        String prompt = promptService.buildPrompt(text, SummaryStyle.CONCISE, null);

        assertThat(prompt).doesNotContain("words");
        assertThat(prompt).doesNotContain("Limit the summary");
    }

    @Test
    @DisplayName("Should return system prompt")
    void shouldReturnSystemPrompt() {
        String systemPrompt = promptService.getSystemPrompt();

        assertThat(systemPrompt).isNotEmpty();
        assertThat(systemPrompt).contains("professional technical writer");
        assertThat(systemPrompt).contains("summarize");
    }

    @Test
    @DisplayName("Should throw exception when text is too short")
    void shouldThrowExceptionWhenTextIsTooShort() {
        String shortText = "Too short";

        assertThatThrownBy(() -> promptService.validateInputLength(shortText))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("at least 100 characters");
    }

    @Test
    @DisplayName("Should throw exception when text is null")
    void shouldThrowExceptionWhenTextIsNull() {
        assertThatThrownBy(() -> promptService.validateInputLength(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should validate text of sufficient length")
    void shouldValidateTextOfSufficientLength() {
        String validText = "A".repeat(100);

        assertThat(promptService.validateInputLength(validText)).isTrue();
    }
}

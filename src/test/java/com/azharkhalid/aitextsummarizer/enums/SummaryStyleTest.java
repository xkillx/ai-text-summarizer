package com.azharkhalid.aitextsummarizer.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("SummaryStyle Enum Tests")
class SummaryStyleTest {

    @Test
    @DisplayName("Should have three enum values")
    void shouldHaveThreeEnumValues() {
        assertThat(SummaryStyle.values()).hasSize(3);
    }

    @Test
    @DisplayName("Should have CONCISE style with description")
    void shouldHaveConciseStyleWithDescription() {
        SummaryStyle style = SummaryStyle.CONCISE;

        assertThat(style.name()).isEqualTo("CONCISE");
        assertThat(style.getPromptSuffix()).contains("brief");
        assertThat(style.getPromptSuffix()).contains("direct");
    }

    @Test
    @DisplayName("Should have BULLET style with description")
    void shouldHaveBulletStyleWithDescription() {
        SummaryStyle style = SummaryStyle.BULLET;

        assertThat(style.name()).isEqualTo("BULLET");
        assertThat(style.getPromptSuffix()).contains("bulleted list");
    }

    @Test
    @DisplayName("Should have EXECUTIVE style with description")
    void shouldHaveExecutiveStyleWithDescription() {
        SummaryStyle style = SummaryStyle.EXECUTIVE;

        assertThat(style.name()).isEqualTo("EXECUTIVE");
        assertThat(style.getPromptSuffix()).contains("executive-level");
        assertThat(style.getPromptSuffix()).contains("strategic");
    }
}

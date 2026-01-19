package com.azharkhalid.aitextsummarizer.validation;

import com.azharkhalid.aitextsummarizer.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("CharacterEncodingValidator Tests")
class CharacterEncodingValidatorTest {

    private CharacterEncodingValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CharacterEncodingValidator();
    }

    @Test
    @DisplayName("Should pass validation for valid UTF-8 text")
    void shouldPassValidationForValidUtf8Text() {
        String validText = "This is a valid text with UTF-8 characters: 你好 🚀";

        assertThatCode(() -> validator.validate(validText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should pass validation for text with common special characters")
    void shouldPassValidationForTextWithCommonSpecialCharacters() {
        String validText = "Valid text with special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";

        assertThatCode(() -> validator.validate(validText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception for null input")
    void shouldThrowExceptionForNullInput() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for text with control characters")
    void shouldThrowExceptionForTextWithControlCharacters() {
        // Create text with null byte and other control characters
        String textWithControlChars = "Valid text\u0000with\u0001control\u0008chars";

        assertThatThrownBy(() -> validator.validate(textWithControlChars))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("control characters");
    }

    @Test
    @DisplayName("Should allow newlines and tabs")
    void shouldAllowNewlinesAndTabs() {
        String validText = "Text with newlines\nand tabs\tis acceptable.";

        assertThatCode(() -> validator.validate(validText))
                .doesNotThrowAnyException();
    }
}

package com.azharkhalid.aitextsummarizer.validation;

import com.azharkhalid.aitextsummarizer.config.SummarizeProperties;
import com.azharkhalid.aitextsummarizer.exception.InvalidInputException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MaxInputSizeValidator Tests")
class MaxInputSizeValidatorTest {

    private MaxInputSizeValidator validator;
    private SummarizeProperties properties;

    @BeforeEach
    void setUp() {
        properties = new SummarizeProperties();
        properties.setMaxInputLength(10000);
        validator = new MaxInputSizeValidator(properties);
    }

    @Test
    @DisplayName("Should pass validation for valid input size")
    void shouldPassValidationForValidInputSize() {
        String validText = "a".repeat(5000);

        assertThatCode(() -> validator.validate(validText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception for input exceeding maximum size")
    void shouldThrowExceptionForInputExceedingMaximumSize() {
        String longText = "a".repeat(10001);

        assertThatThrownBy(() -> validator.validate(longText))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("exceeds maximum length");
    }

    @Test
    @DisplayName("Should throw exception for null input")
    void shouldThrowExceptionForNullInput() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should pass minimum length validation")
    void shouldPassMinimumLengthValidation() {
        String validText = "a".repeat(100);

        assertThatCode(() -> validator.validateMinimumLength(validText))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should throw exception for input below minimum length")
    void shouldThrowExceptionForInputBelowMinimumLength() {
        String shortText = "a".repeat(50);

        assertThatThrownBy(() -> validator.validateMinimumLength(shortText))
                .isInstanceOf(InvalidInputException.class)
                .hasMessageContaining("at least 100 characters");
    }
}

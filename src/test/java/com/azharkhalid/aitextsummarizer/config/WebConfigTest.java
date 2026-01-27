package com.azharkhalid.aitextsummarizer.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("WebConfig Tests")
@SpringBootTest
class WebConfigTest {

    @Autowired
    private WebConfig webConfig;

    @Test
    @DisplayName("Should create WebConfig bean")
    void shouldCreateWebConfigBean() {
        assertThat(webConfig).isNotNull();
        assertThat(webConfig).isInstanceOf(WebMvcConfigurer.class);
    }
}

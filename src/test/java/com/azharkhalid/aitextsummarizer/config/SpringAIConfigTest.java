package com.azharkhalid.aitextsummarizer.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SpringAIConfig Tests")
@SpringBootTest
class SpringAIConfigTest {

    @Autowired
    private ChatClient chatClient;

    @Test
    @DisplayName("Should create ChatClient bean")
    void shouldCreateChatClientBean() {
        assertThat(chatClient).isNotNull();
    }
}

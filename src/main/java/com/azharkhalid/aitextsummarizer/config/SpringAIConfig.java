package com.azharkhalid.aitextsummarizer.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Spring AI integration with OpenAI.
 *
 * Note: When using spring-ai-starter-model-openai, most beans are auto-configured.
 * We only need to explicitly define the ChatClient bean with our customizations.
 */
@Configuration
public class SpringAIConfig {

    /**
     * Creates the ChatClient bean using the auto-configured OpenAiChatModel.
     * The OpenAiChatModel is automatically created by Spring Boot based on
     * application.yml properties (spring.ai.openai.api-key, etc.)
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }
}

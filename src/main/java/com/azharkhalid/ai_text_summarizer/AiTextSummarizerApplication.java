package com.azharkhalid.ai_text_summarizer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class AiTextSummarizerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiTextSummarizerApplication.class, args);
	}

}

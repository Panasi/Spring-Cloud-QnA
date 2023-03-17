package com.panasi.qna.answer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AnswerConfig {
	@Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

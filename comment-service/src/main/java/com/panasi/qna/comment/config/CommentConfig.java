package com.panasi.qna.comment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CommentConfig {
	@Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

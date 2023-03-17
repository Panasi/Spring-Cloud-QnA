package com.panasi.qna.category.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CategoryConfig {
	@Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

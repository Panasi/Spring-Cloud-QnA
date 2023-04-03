package com.panasi.qna.question.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class RabbitMQConfig {
	
	@Bean
	MessageConverter messageConverter(Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder) {
	    return new Jackson2JsonMessageConverter(jackson2ObjectMapperBuilder.build());
	}

	@Bean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
	    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
	    rabbitTemplate.setMessageConverter(messageConverter);
	    return rabbitTemplate;
	}
	
	@Bean
	Queue getAllSubcategoryIdQueue() {
	    return new Queue("getAllSubcategoryIdQueue");
	}
	
	@Bean
	Queue isCategoryExistsQueue() {
	    return new Queue("isCategoryExistsQueue");
	}
	
	@Bean
	Queue getCategoryNameQueue() {
	    return new Queue("getCategoryNameQueue");
	}
	
	@Bean
	Queue getAnswersByQuestionQueue() {
	    return new Queue("getAnswersByQuestionQueue");
	}
	
	@Bean
	Queue getAnswersByQuestionAndAuthorQueue() {
	    return new Queue("getAnswersByQuestionAndAuthorQueue");
	}
	
	@Bean
	Queue getQuestionRatingQueue() {
	    return new Queue("getQuestionRatingQueue");
	}
	
	@Bean
	Queue getQuestionsPDFQueue() {
	    return new Queue("getQuestionsPDFQueue");
	}

}

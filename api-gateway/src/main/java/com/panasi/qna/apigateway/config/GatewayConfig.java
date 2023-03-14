package com.panasi.qna.apigateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.panasi.qna.apigateway.filter.JwtAuthenticationFilter;

@Configuration
public class GatewayConfig {

	@Autowired
	private JwtAuthenticationFilter filter;

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("auth", r -> r.path("/auth/**").filters(f -> f.filter(filter)).uri("lb://security-service"))
				.route("admin category",
						r -> r.path("/admin/categories/**").filters(f -> f.filter(filter)).uri("lb://category-service"))
				.route("user category",
						r -> r.path("/categories/**").filters(f -> f.filter(filter)).uri("lb://category-service"))
				.route("external category",
						r -> r.path("/external/categories/**").filters(f -> f.filter(filter))
								.uri("lb://category-service"))
				.route("admin question",
						r -> r.path("/admin/questions/**").filters(f -> f.filter(filter)).uri("lb://question-service"))
				.route("user question",
						r -> r.path("/questions/**").filters(f -> f.filter(filter)).uri("lb://question-service"))
				.route("external question",
						r -> r.path("/external/questions/**").filters(f -> f.filter(filter))
								.uri("lb://question-service"))
				.route("admin answer",
						r -> r.path("/admin/answers/**").filters(f -> f.filter(filter)).uri("lb://answer-service"))
				.route("user answer",
						r -> r.path("/answers/**").filters(f -> f.filter(filter)).uri("lb://answer-service"))
				.route("external answer",
						r -> r.path("/external/answers/**").filters(f -> f.filter(filter)).uri("lb://answer-service"))
				.route("admin comment",
						r -> r.path("/admin/comments/**").filters(f -> f.filter(filter)).uri("lb://comment-service"))
				.route("user comment",
						r -> r.path("/comments/**").filters(f -> f.filter(filter)).uri("lb://comment-service"))
				.route("external comment",
						r -> r.path("/external/comments/**").filters(f -> f.filter(filter)).uri("lb://comment-service"))
				.route("pdf", r -> r.path("/pdf/**").filters(f -> f.filter(filter)).uri("lb://pdf-service"))
				.build();
	}

}

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
				.route("helloworld", r -> r.path("/helloworld").filters(f -> f.filter(filter)).uri("lb://eureka-client"))
				.route("admin category", r -> r.path("/admin/categories/**").filters(f -> f.filter(filter)).uri("lb://category-service"))
				.build();
	}

}

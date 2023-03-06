package com.panasi.qna.apigateway.filter;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.panasi.qna.apigateway.util.JwtUtils;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

	private static final List<String> SECURED_API_ENDPOINTS = List.of("admin/categories", "admin/questions");
    
    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpMethod httpMethod = request.getMethod();

        if (isApiSecured(request) || !HttpMethod.GET.equals(httpMethod)) {
            String authToken = extractAuthHeader(request);
            if (authToken == null) {
                return handleUnauthorizedAccessAttempt(exchange, "Unauthorized");
            } else if (!jwtUtils.validateJwtToken(authToken)) {
                return handleUnauthorizedAccessAttempt(exchange, "Invalid token");
            }
        }

        return chain.filter(exchange);
    }

    private boolean isApiSecured(ServerHttpRequest request) {
        return SECURED_API_ENDPOINTS.stream()
                .anyMatch(uri -> request.getURI().getPath().contains(uri));
    }

    private String extractAuthHeader(ServerHttpRequest request) {
        List<String> authHeaders = request.getHeaders().getOrDefault("Authorization", Collections.emptyList());
        if (!authHeaders.isEmpty()) {
            return  authHeaders.get(0);
        }
        return null;
    }

    private Mono<Void> handleUnauthorizedAccessAttempt(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        String responseBody = "Unauthorized access attempt: " + message;
        response.getHeaders().add("Content-Type", "application/json");
        byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer))
                .doOnError(error -> DataBufferUtils.release(buffer));
    }

}

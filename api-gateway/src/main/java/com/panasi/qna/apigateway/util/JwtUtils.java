package com.panasi.qna.apigateway.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtils {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${auth.jwtSecret}")
	private String jwtSecret;
	private final RabbitTemplate rabbitTemplate;

	public boolean validateJwtToken(String authHeader) {
		
		String jwt = parseJwt(authHeader);
		
		if (jwt == null) {
			logger.error("Invalid JWT type: {}", "Token does not have a prefix or an invalid prefix");
			return false;
		}
		
		boolean isJwtRevoked = checkJwtRevoked(jwt);
		
		if (isJwtRevoked) {
			logger.error("Jwt is revoked");
			return false;
		}
		
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
			return true;
		} catch (SignatureException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
		
	}

	private String parseJwt(String authHeader) {
		
		if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7, authHeader.length());
		}
		
		return null;
		
	}
	
	private Boolean checkJwtRevoked(String jwt) {
		
		return (Boolean) rabbitTemplate.convertSendAndReceive("isJwtRevokedQueue", jwt);
		
	}

}

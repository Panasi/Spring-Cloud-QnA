package com.panasi.qna.security.util;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.panasi.qna.security.entity.Role;
import com.panasi.qna.security.entity.User;
import com.panasi.qna.security.payload.ERole;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtils {

	@Value("${auth.jwtSecret}")
	private String jwtSecret;

	@Value("${auth.jwtExpirationMs}")
	private int jwtExpirationMs;

	public String generateJwtToken(User user) {
		List<ERole> roles = user.getRoles().stream().map(Role::getName).toList();
		
		return Jwts.builder()
				.setId(Integer.toString((user.getId())))
				.setSubject((user.getUsername()))
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}
	
	public Integer getUserIdFromJwt(String jwt) {
		
		return Integer.valueOf(Jwts.parser()
				.setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getId());
		
	}

}

package com.panasi.qna.security.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponse {

	private String token;
	private String type = "Bearer";
	private Integer id;
	private String username;
	private String email;
	private List<String> roles;

	public JwtResponse(String accessToken, Integer id, String username, String email, List<String> roles) {
		this.token = accessToken;
		this.id = id;
		this.username = username;
		this.email = email;
		this.roles = roles;
	}

}

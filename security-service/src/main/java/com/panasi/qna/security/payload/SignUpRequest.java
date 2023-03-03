package com.panasi.qna.security.payload;

import java.util.Set;

import lombok.Data;

@Data
public class SignUpRequest {
	
	private String username;
	private String email;
	private String password;
	private Set<String> roles;

}

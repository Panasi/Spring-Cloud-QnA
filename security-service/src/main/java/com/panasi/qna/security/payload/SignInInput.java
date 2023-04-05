package com.panasi.qna.security.payload;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SignInInput {
	
	@NotNull
	private String username;
	
	@NotNull
	private String password;

}

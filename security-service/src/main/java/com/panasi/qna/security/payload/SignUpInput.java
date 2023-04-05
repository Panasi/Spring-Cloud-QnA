package com.panasi.qna.security.payload;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class SignUpInput {
	
	@NotNull
	@Size(min = 3, max = 15)
	private String username;
	
	@NotNull
	@Email
	private String email;
	
	@NotNull
	@Size(min = 8, max = 20)
	private String password;
	
	@NotNull
	private Set<String> roles;

}

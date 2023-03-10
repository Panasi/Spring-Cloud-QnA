package com.panasi.qna.security.payload;

import lombok.Data;

@Data
public class SignInRequest {

	private String username;
	private String password;

}

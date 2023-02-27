package com.panasi.qna.security.exception;

public class JwtTokenMalformedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public JwtTokenMalformedException(String errorMessage) {
		super(errorMessage);
	}

}

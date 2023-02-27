package com.panasi.qna.security.exception;

public class JwtTokenMissingException extends Exception {
	
	private static final long serialVersionUID = 1L;
	
	public JwtTokenMissingException(String errorMessage) {
		super(errorMessage);
	}
	
}

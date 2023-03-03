package com.panasi.qna.security.exception;

public class DuplicateRegistrationException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public DuplicateRegistrationException(String message) {
        super(message);
    }
}

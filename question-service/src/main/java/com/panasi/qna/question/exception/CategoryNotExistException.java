package com.panasi.qna.question.exception;

public class CategoryNotExistException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public CategoryNotExistException(String message) {
        super(message);
    }
}

package com.panasi.qna.category.exception;

public class CategoryIsNotEmptyException extends Exception {

	private static final long serialVersionUID = 1L;

	public CategoryIsNotEmptyException(String message) {
		super(message);
	}
}

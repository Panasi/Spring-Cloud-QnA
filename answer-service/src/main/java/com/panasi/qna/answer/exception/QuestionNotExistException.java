package com.panasi.qna.answer.exception;

public class QuestionNotExistException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public QuestionNotExistException(String message) {
        super(message);
    }
}

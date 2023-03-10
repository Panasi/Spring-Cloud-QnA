package com.panasi.qna.answer.payload;

import lombok.Data;

@Data
public class AnswerRequest {

	private Integer questionId;
	private Boolean isPrivate;
	private String content;

}

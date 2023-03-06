package com.panasi.qna.question.payload;

import lombok.Data;

@Data
public class QuestionRequest {
	
	private Integer categoryId;
	private Boolean isPrivate;
	private String content;

}

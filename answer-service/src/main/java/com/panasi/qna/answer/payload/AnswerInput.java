package com.panasi.qna.answer.payload;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class AnswerInput {
	
	@NotNull
	private Integer questionId;
	
	@NotNull
	private Boolean isPrivate;
	
	@NotNull
	@Size(min = 10, max = 500)
	private String content;

}

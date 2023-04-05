package com.panasi.qna.question.payload;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class QuestionInput {
	
	@NotNull
	private Integer categoryId;
	
	@NotNull
	private Boolean isPrivate;
	
	@NotNull
	@Size(min = 10, max = 500)
	private String content;

}

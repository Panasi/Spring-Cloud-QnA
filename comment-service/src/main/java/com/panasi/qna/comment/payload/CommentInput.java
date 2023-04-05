package com.panasi.qna.comment.payload;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Data;

@Data
public class CommentInput {
	
	@NotNull
	private Integer targetId;
	
	@NotNull
	@Size(min = 3, max = 500)
	private String content;
	
	@NotNull
	@Min(1)
	@Max(5)
	private Integer rate;

}

package com.panasi.qna.comment.payload;

import lombok.Data;

@Data
public class CommentRequest {
	
	private String content;
	private Integer rate;

}

package com.panasi.qna.comment.dto;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnswerCommentDTO {
	
	private Integer id;
	private Integer answerId;
	private String content;
	private Integer rate;
	private Integer authorId;
	private LocalDateTime date;

}

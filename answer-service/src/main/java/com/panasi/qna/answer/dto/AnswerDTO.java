package com.panasi.qna.answer.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AnswerDTO {
	
	private Integer id;
	private Integer questionId;
	private Boolean isPrivate;
	private String content;
	private Integer authorId;
	private LocalDateTime date;

}

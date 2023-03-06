package com.panasi.qna.question.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionDTO {
	
	private Integer id;
	private Integer categoryId;
	private Boolean isPrivate;
	private String content;
	private Integer authorId;
	private LocalDateTime date;

}

package com.panasi.qna.question.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionWithAnswersDTO {

	private Integer id;
	private Integer categoryId;
	private Boolean isPrivate;
	private String content;
	private Integer authorId;
	private LocalDateTime date;
	private Double rating;
	private List<AnswerDTO> answers;

}

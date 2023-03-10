package com.panasi.qna.answer.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "answers")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "question_id", nullable = false)
	private Integer questionId;

	@Column(name = "is_private")
	private Boolean isPrivate;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "user_id")
	private Integer authorId;

	@Column(name = "date")
	private LocalDateTime date;

}

package com.panasi.qna.comment.entity;

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
@Table(name = "question_comments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuestionComment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "question_id", nullable = false)
	private Integer questionId;

	@Column(name = "content")
	private String content;

	@Column(name = "rate", nullable = false)
	private Integer rate;

	@Column(name = "user_id")
	private Integer authorId;

	@Column(name = "date")
	private LocalDateTime date;

}

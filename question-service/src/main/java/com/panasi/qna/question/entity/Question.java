package com.panasi.qna.question.entity;

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
@Table(name = "questions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "category_id", nullable = false)
	private Integer categoryId;

	@Column(name = "is_private")
	private Boolean isPrivate;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "user_id")
	private Integer authorId;

	@Column(name = "date")
	private LocalDateTime date;

}

package com.panasi.qna.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panasi.qna.comment.entity.AnswerComment;

public interface AnswerCommentRepository extends JpaRepository<AnswerComment, Integer> {

	public List<AnswerComment> findAllByAnswerId(int answerId);

	public List<AnswerComment> findAllByAuthorId(int authorId);

	@Query("SELECT AVG(rate) FROM AnswerComment c WHERE c.answerId = ?1")
	public Double getRating(int answerId);

}

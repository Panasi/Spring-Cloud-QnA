package com.panasi.qna.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panasi.qna.comment.entity.QuestionComment;

public interface QuestionCommentRepository extends JpaRepository<QuestionComment, Integer> {

	public List<QuestionComment> findAllByQuestionId(int questionId);

	public List<QuestionComment> findAllByAuthorId(int authorId);

	@Query("SELECT AVG(rate) FROM QuestionComment c WHERE c.questionId = ?1")
	public Double getRating(int questionId);

}

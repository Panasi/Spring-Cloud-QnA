package com.panasi.qna.question.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panasi.qna.question.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

	public List<Question> findAllByIsPrivate(boolean isPrivate);

	public List<Question> findAllByCategoryId(int categoryId);

	public List<Question> findAllByCategoryIdAndIsPrivate(int categoryId, boolean isPrivate);

	public List<Question> findAllByAuthorId(int authorId);

	public List<Question> findAllByAuthorIdAndIsPrivate(int authorId, boolean isPrivate);

	public List<Question> findAllByCategoryIdAndIsPrivateAndAuthorId(int categoryId, boolean isPrivate, int authorId);

	@Query("SELECT q FROM Question q WHERE (q.isPrivate = false) OR (q.isPrivate = true AND q.authorId = ?1)")
	public List<Question> findAllPublicAndAuthorPrivate(int authorId);

	@Query("SELECT q FROM Question q WHERE (q.categoryId = ?1 AND q.isPrivate = false) OR (q.categoryId = ?1 AND q.isPrivate = true AND q.authorId = ?2)")
	public List<Question> findAllPublicAndAuthorPrivateByCategoryId(int categoryId, int authorId);

	@Query("SELECT q.id FROM Question q WHERE q.categoryId = ?1")
	public List<Integer> findAllQuestionIdByCategory(int categoryId);
	
	@Query("SELECT q.authorId FROM Question q")
	public List<Integer> findAllAuthorId();
	
	public Integer countByCategoryId(int categoryId);

}

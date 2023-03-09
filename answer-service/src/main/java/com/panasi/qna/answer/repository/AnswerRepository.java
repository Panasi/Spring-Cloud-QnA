package com.panasi.qna.answer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panasi.qna.answer.entity.Answer;


public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	
	public List<Answer> findAllByQuestionId(int questionId);
	
	public List<Answer> findAllByIsPrivate(Boolean isPrivate);
	
	public List<Answer> findAllByAuthorId(int authorId);
	
	public List<Answer> findAllByAuthorIdAndIsPrivate(int authorId, Boolean isPrivate);
	
	@Query("SELECT a FROM Answer a WHERE (a.questionId = ?1 AND a.isPrivate = false) OR (a.questionId = ?1 AND a.isPrivate = true AND a.authorId = ?2)")
	public List<Answer> findAllByQuestionIdandAuthorId(int questionId, int authorId);
	
	public boolean findIsPrivateById(int id);
	
	public int findAuthorIdById(int id);
	
}

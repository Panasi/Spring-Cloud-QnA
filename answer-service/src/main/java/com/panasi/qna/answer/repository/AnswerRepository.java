package com.panasi.qna.answer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.panasi.qna.answer.entity.Answer;


public interface AnswerRepository extends JpaRepository<Answer, Integer> {
	
	public List<Answer> findAllByQuestionId(int questionId);
	
	public List<Answer> findAllByIsPrivate(Boolean isPrivate);
	
	public List<Answer> findAllByAuthorId(int authorId);
	
	public List<Answer> findAllByAuthorIdAndIsPrivate(int authorId, Boolean isPrivate);
	
	public boolean findIsPrivateById(int id);
	
	public int findAuthorIdById(int id);
	
}

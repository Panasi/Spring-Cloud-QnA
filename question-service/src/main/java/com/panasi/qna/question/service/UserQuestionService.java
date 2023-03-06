package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.exception.QuestionUpdateException;
import com.panasi.qna.question.payload.QuestionRequest;
import com.panasi.qna.question.payload.Utils;

@Service
public class UserQuestionService extends QuestionService {

	// Return questions from certain category
	public List<QuestionDTO> getCategoryQuestions(int categoryId, String access) {
		int currentUserId = Utils.getCurrentUserId();
		List<Question> questions;
		if (access.equals(PUBLIC)) {
			questions = questionRepository.findAllByCategoryIdAndIsPrivate(categoryId, false);
		} else if (access.equals(PRIVATE)) {
		    questions = questionRepository.findAllByCategoryIdAndIsPrivateAndAuthorId(categoryId, true, currentUserId);
		} else {
			questions = questionRepository.findAllPublicAndAuthorPrivateByCategoryId(categoryId, currentUserId);
		}
		return questionMapper.toQuestionDTOs(questions);
	}
		
	// Return questions from certain category and all its subcategories
	public List<QuestionDTO> getSubcategoriesQuestions(int categoryId, String access, List<QuestionDTO> allQuestionDTOs) {
		List<QuestionDTO> questionDtos = getCategoryQuestions(categoryId, access);
		allQuestionDTOs.addAll(questionDtos);
		
		List<Integer> allSubcategoriesId = getAllSubcategoryId(categoryId);
		
		if (allSubcategoriesId.isEmpty()) {
			return allQuestionDTOs;
		}
		allSubcategoriesId.forEach(id -> getSubcategoriesQuestions(id, access, allQuestionDTOs));
		return allQuestionDTOs;
	}
		
	// Return user questions
	public List<QuestionDTO> getUserQuestions(int authorId, String access) {
	    int currentUserId = Utils.getCurrentUserId();
	    List<Question> questions;

	    if (access.equals(PRIVATE) && authorId != currentUserId) {
	        return new ArrayList<>();
	    }

	    if (access.equals(PUBLIC) || (access.equals(ALL) && authorId != currentUserId)) {
	        questions = questionRepository.findAllByAuthorIdAndIsPrivate(authorId, false);
	    } else if (access.equals(PRIVATE)) {
	        questions = questionRepository.findAllByAuthorIdAndIsPrivate(authorId, true);
	    } else {
	        questions = questionRepository.findAllByAuthorId(authorId);
	    }

	    return questionMapper.toQuestionDTOs(questions); 
	}
		
	// Update certain question
	public void updateQuestion(QuestionRequest questionRequest, int questionId) throws NotFoundException, QuestionUpdateException {
	    int currentUserId = Utils.getCurrentUserId();
	    Question question = questionRepository.findById(questionId)
	            .orElseThrow(NotFoundException::new);
	    if (question.getAuthorId() != currentUserId) {
	    	throw new QuestionUpdateException("You can't update other users questions");
	    }
	    LocalDateTime dateTime = LocalDateTime.now();
	    question.setDate(dateTime);
	    if (Objects.nonNull(questionRequest.getContent())) {
	        question.setContent(questionRequest.getContent());
	    }
	    if (Objects.nonNull(questionRequest.getCategoryId())) {
	        question.setCategoryId(questionRequest.getCategoryId());
	    }
	    if (Objects.nonNull(questionRequest.getIsPrivate())) {
	        question.setIsPrivate(questionRequest.getIsPrivate());
	    }
	    questionRepository.save(question);
	}

}

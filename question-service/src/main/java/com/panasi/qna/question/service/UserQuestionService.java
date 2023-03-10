package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panasi.qna.question.dto.AnswerDTO;
import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.exception.ForbiddenException;
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
		List<QuestionDTO> questionsDTO = questionMapper.toQuestionDTOs(questions);
	    questionsDTO.forEach(questionDTO -> questionDTO.setRating(getRating(questionDTO.getId())));
	    return sortQuestionsDTO(questionsDTO);
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

	    List<QuestionDTO> questionsDTO = questionMapper.toQuestionDTOs(questions);
	    questionsDTO.forEach(questionDTO -> questionDTO.setRating(getRating(questionDTO.getId())));
	    return sortQuestionsDTO(questionsDTO); 
	}
	
	// Return question by id
	public QuestionWithAnswersDTO getQuestionById(int questionId) throws NotFoundException, ForbiddenException {
		Question question = questionRepository.findById(questionId)
				.orElseThrow(NotFoundException::new);
		
		boolean isQuestionPrivate = question.getIsPrivate();
		int questionAuthorId = question.getAuthorId();
		int currentUserId = Utils.getCurrentUserId();
		
		if (isQuestionPrivate && questionAuthorId != currentUserId) {
			throw new ForbiddenException("You can't get another private question");
		}
		QuestionWithAnswersDTO questionDTO = fullQuestionMapper.toFullQuestionDTO(question);
		questionDTO.setRating(getRating(questionDTO.getId()));
		List<AnswerDTO> answers = getAnswersByQuestionAndAuthor(questionId, currentUserId);
		questionDTO.setAnswers(answers);
		return questionDTO;
	}
		
	// Update certain question
	@Transactional
	public void updateQuestion(QuestionRequest questionRequest, int questionId) throws NotFoundException, ForbiddenException {
	    int currentUserId = Utils.getCurrentUserId();
	    Question question = questionRepository.findById(questionId)
	            .orElseThrow(NotFoundException::new);
	    
	    if (question.getAuthorId() != currentUserId) {
	    	throw new ForbiddenException("You can't update another users questions");
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

package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panasi.qna.question.dto.AnswerDTO;
import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.payload.QuestionRequest;

@Service
public class AdminQuestionService extends QuestionService {

	// Return all questions
	public List<QuestionDTO> getAllQuestions(String access) {
		List<Question> questions;
		if (access.equals(PUBLIC)) {
			questions = questionRepository.findAllByIsPrivate(false);
		} else if (access.equals(PRIVATE)) {
			questions = questionRepository.findAllByIsPrivate(true);
		} else {
			questions = questionRepository.findAll();
		}
		List<QuestionDTO> questionsDTO = questionMapper.toQuestionDTOs(questions);
		questionsDTO.forEach(questionDTO -> questionDTO.setRating(getRating(questionDTO.getId())));
		return sortQuestionsDTO(questionsDTO);
	}

	// Return questions from certain category
	public List<QuestionDTO> getCategoryQuestions(int categoryId, String access) {
		List<Question> questions;
		if (access.equals(PUBLIC)) {
			questions = questionRepository.findAllByCategoryIdAndIsPrivate(categoryId, false);
		} else if (access.equals(PRIVATE)) {
			questions = questionRepository.findAllByCategoryIdAndIsPrivate(categoryId, true);
		} else {
			questions = questionRepository.findAllByCategoryId(categoryId);
		}
		List<QuestionDTO> questionsDTO = questionMapper.toQuestionDTOs(questions);
		questionsDTO.forEach(questionDTO -> questionDTO.setRating(getRating(questionDTO.getId())));
		return sortQuestionsDTO(questionsDTO);
	}

	// Return questions from certain category and all its subcategories
	public List<QuestionDTO> getSubcategoriesQuestions(int categoryId, String access,
			List<QuestionDTO> allQuestionDTOs) {
		List<QuestionDTO> questionDTOs = getCategoryQuestions(categoryId, access);
		allQuestionDTOs.addAll(questionDTOs);

		List<Integer> allSubcategoriesId = getAllSubcategoryId(categoryId);

		if (allSubcategoriesId.isEmpty()) {
			return allQuestionDTOs;
		}
		allSubcategoriesId.forEach(id -> getSubcategoriesQuestions(id, access, allQuestionDTOs));
		return allQuestionDTOs;
	}

	// Return user questions
	public List<QuestionDTO> getUserQuestions(int authorId, String access) {
		List<Question> questions;
		if (access.equals(PUBLIC)) {
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
	public QuestionWithAnswersDTO getQuestionById(int questionId) throws NotFoundException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);
		QuestionWithAnswersDTO questionDTO = fullQuestionMapper.toFullQuestionDTO(question);
		questionDTO.setRating(getRating(questionDTO.getId()));
		List<AnswerDTO> answers = getAnswersByQuestion(questionId);
		questionDTO.setAnswers(answers);
		return questionDTO;
	}

	// Update certain question
	@Transactional
	public void updateQuestion(QuestionRequest questionRequest, int questionId) throws NotFoundException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);
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

	// Delete certain question
	public void deleteQuestion(int questionId) {
		questionRepository.deleteById(questionId);
	}

}

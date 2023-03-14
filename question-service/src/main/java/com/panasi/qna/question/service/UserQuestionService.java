package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panasi.qna.question.dto.AnswerDTO;
import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.exception.DownloadException;
import com.panasi.qna.question.exception.ForbiddenException;
import com.panasi.qna.question.payload.QuestionRequest;
import com.panasi.qna.question.payload.Utils;

@Service
public class UserQuestionService extends QuestionService {

	// Return questions with answers
	public List<QuestionWithAnswersDTO> getFullQuestions() {
		int currentUserId = Utils.getCurrentUserId();
		List<Question> questions = questionRepository.findAllPublicAndAuthorPrivate(currentUserId);
		List<QuestionWithAnswersDTO> questionsDTO = fullQuestionMapper.toQuestionWithAnswersListDTO(questions);
		questionsDTO.forEach(questionDTO -> {
			questionDTO.setRating(getRating(questionDTO.getId()));
			List<AnswerDTO> answers = getAnswersByQuestionAndAuthor(questionDTO.getId(), currentUserId);
			questionDTO.setAnswers(answers);
		});
		return sortQuestionsWithAnswersDTO(questionsDTO);
	}

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

	// Return questions with answers from certain category
	public List<QuestionWithAnswersDTO> getFullCategoryQuestions(int categoryId) {
		int currentUserId = Utils.getCurrentUserId();
		List<Question> questions = questionRepository.findAllPublicAndAuthorPrivateByCategoryId(categoryId,
				currentUserId);
		List<QuestionWithAnswersDTO> questionsDTO = fullQuestionMapper.toQuestionWithAnswersListDTO(questions);
		questionsDTO.forEach(questionDTO -> {
			questionDTO.setRating(getRating(questionDTO.getId()));
			List<AnswerDTO> answers = getAnswersByQuestionAndAuthor(questionDTO.getId(), currentUserId);
			questionDTO.setAnswers(answers);
		});
		return sortQuestionsWithAnswersDTO(questionsDTO);
	}

	// Return questions from certain category and all its subcategories
	public List<QuestionDTO> getSubcategoriesQuestions(int categoryId, String access,
			List<QuestionDTO> allQuestionDTOs) {
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

	// Return question with answers by id
	public QuestionWithAnswersDTO getQuestionById(int questionId) throws NotFoundException, ForbiddenException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);

		boolean isQuestionPrivate = question.getIsPrivate();
		int questionAuthorId = question.getAuthorId();
		int currentUserId = Utils.getCurrentUserId();

		if (isQuestionPrivate && questionAuthorId != currentUserId) {
			throw new ForbiddenException("You can't get another private question");
		}
		QuestionWithAnswersDTO questionDTO = fullQuestionMapper.toQuestionWithAnswersDTO(question);
		questionDTO.setRating(getRating(questionDTO.getId()));
		List<AnswerDTO> answers = getAnswersByQuestionAndAuthor(questionId, currentUserId);
		questionDTO.setAnswers(answers);
		return questionDTO;
	}

	// Update certain question
	@Transactional
	public void updateQuestion(QuestionRequest questionRequest, int questionId)
			throws NotFoundException, ForbiddenException {
		int currentUserId = Utils.getCurrentUserId();
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);

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

	// Return PDF file for questions with answers
	public byte[] getQuestionsPDF() throws DownloadException {

		String url = "http://localhost:8765/pdf/download";

		List<QuestionWithAnswersDTO> questions = getFullQuestions();

		if (questions.isEmpty()) {
			throw new DownloadException("Questions doesn't exist");
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("fileName", "Questions and Answers.pdf");
		headers.add("title", "Questions and answers");

		HttpEntity<List<QuestionWithAnswersDTO>> request = new HttpEntity<>(questions, headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, request, byte[].class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new DownloadException("Error downloading file");
		}
		return response.getBody();
	}

	// Return PDF file for questions with answers from category
	public byte[] getCategoryQuestionsPDF(int categoryId) throws DownloadException {

		String url = "http://localhost:8765/pdf/download";

		List<QuestionWithAnswersDTO> questions = getFullCategoryQuestions(categoryId);

		if (questions.isEmpty()) {
			throw new DownloadException("Category is empty or doesn't exist");
		}

		String categoryName = getCategoryName(categoryId);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("fileName", "Questions from Category.pdf");
		headers.add("title", "Questions and answers from " + categoryName + " category");

		HttpEntity<List<QuestionWithAnswersDTO>> request = new HttpEntity<>(questions, headers);
		ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.POST, request, byte[].class);

		if (response.getStatusCode() != HttpStatus.OK) {
			throw new DownloadException("Error downloading file");
		}
		return response.getBody();
	}

}

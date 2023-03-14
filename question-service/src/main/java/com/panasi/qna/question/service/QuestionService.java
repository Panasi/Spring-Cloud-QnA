package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.question.dto.AnswerDTO;
import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.mapper.FullQuestionMapper;
import com.panasi.qna.question.mapper.QuestionMapper;
import com.panasi.qna.question.payload.QuestionRequest;
import com.panasi.qna.question.payload.Utils;
import com.panasi.qna.question.repository.QuestionRepository;

@Service
public class QuestionService {

	@Autowired
	protected QuestionRepository questionRepository;
	@Autowired
	protected QuestionMapper questionMapper;
	@Autowired
	protected FullQuestionMapper fullQuestionMapper;
	protected RestTemplate restTemplate = new RestTemplate();

	protected static final String PUBLIC = "public";
	protected static final String PRIVATE = "private";
	protected static final String ALL = "all";

	// Add a new question
	public void createQuestion(QuestionRequest questionRequest) throws NotFoundException {
		if (!isCategoryExists(questionRequest.getCategoryId())) {
			throw new NotFoundException();
		}

		int currentUserId = Utils.getCurrentUserId();
		LocalDateTime dateTime = LocalDateTime.now();
		QuestionDTO questionDTO = QuestionDTO.builder().content(questionRequest.getContent())
				.categoryId(questionRequest.getCategoryId()).isPrivate(questionRequest.getIsPrivate())
				.authorId(currentUserId).date(dateTime).build();
		Question question = questionMapper.toQuestion(questionDTO);
		questionRepository.save(question);
	}

	// Sort questions
	public List<QuestionDTO> sortQuestionsDTO(List<QuestionDTO> questionsDTO) {
		List<QuestionDTO> sortedQuestionsDTO = new ArrayList<>(questionsDTO);
		sortedQuestionsDTO.sort((q1, q2) -> {
			int compare = q1.getCategoryId().compareTo(q2.getCategoryId());
			if (compare == 0) {
				compare = Boolean.compare(q1.getIsPrivate(), q2.getIsPrivate());
				if (compare == 0) {
					compare = Optional.ofNullable(q1.getRating()).orElse(Double.MIN_VALUE)
							.compareTo(Optional.ofNullable(q2.getRating()).orElse(Double.MIN_VALUE));
					if (compare == 0) {
						compare = q1.getDate().compareTo(q2.getDate());
					}
				}
			}
			return compare;
		});
		return sortedQuestionsDTO;
	}

	// Sort questions with answers
	public List<QuestionWithAnswersDTO> sortQuestionsWithAnswersDTO(List<QuestionWithAnswersDTO> questionsDTO) {
		List<QuestionWithAnswersDTO> sortedQuestionsDTO = new ArrayList<>(questionsDTO);
		sortedQuestionsDTO.sort((q1, q2) -> {
			int compare = q1.getCategoryId().compareTo(q2.getCategoryId());
			if (compare == 0) {
				compare = Boolean.compare(q1.getIsPrivate(), q2.getIsPrivate());
				if (compare == 0) {
					compare = Optional.ofNullable(q1.getRating()).orElse(Double.MIN_VALUE)
							.compareTo(Optional.ofNullable(q2.getRating()).orElse(Double.MIN_VALUE));
					if (compare == 0) {
						compare = q1.getDate().compareTo(q2.getDate());
					}
				}
			}
			return compare;
		});
		return sortedQuestionsDTO;
	}

	// Return list of subcategory id
	public List<Integer> getAllSubcategoryId(int parentId) {
		String url = "http://localhost:8765/external/categories/subcategoriesId/" + parentId;
		ResponseEntity<List<Integer>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<Integer>>() {
				});
		return response.getBody();
	}

	// Return is category exists
	public boolean isCategoryExists(int categoryId) {
		String url = "http://localhost:8765/external/categories/exists/" + categoryId;
		ResponseEntity<Boolean> response = restTemplate.exchange(url, HttpMethod.GET, null, Boolean.class);
		return response.getBody();
	}
	
	// Return category name
	public String getCategoryName(int categoryId) {
		String url = "http://localhost:8765/external/categories/" + categoryId + "/name";
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
		return response.getBody();
	}

	// Return list of answers by question
	public List<AnswerDTO> getAnswersByQuestion(int questionId) {
		String url = "http://localhost:8765/external/answers/question?questionId=" + questionId;
		ResponseEntity<List<AnswerDTO>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AnswerDTO>>() {
				});
		return response.getBody();
	}

	// Return list of answers by question and author
	public List<AnswerDTO> getAnswersByQuestionAndAuthor(int questionId, int authorId) {
		String url = "http://localhost:8765/external/answers/question?questionId=" + questionId + "&authorId="
				+ authorId;
		ResponseEntity<List<AnswerDTO>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<AnswerDTO>>() {
				});
		return response.getBody();
	}

	// Return question rating
	public Double getRating(int questionId) {
		String url = "http://localhost:8765/external/comments/question/rating/" + questionId;
		ResponseEntity<Double> response = restTemplate.exchange(url, HttpMethod.GET, null, Double.class);
		Double rating = response.getBody();
		return rating != null ? (Math.ceil(rating * 100) / 100) : null;
	}

	// Return list of question id by category
	public List<Integer> getAllQuestionIdByCategory(int categoryId) {
		return questionRepository.findAllQuestionIdByCategory(categoryId);
	}

	// Return is question exists
	public boolean isQuestionExists(int questionId) {
		return questionRepository.existsById(questionId);
	}

	// Return question isPrivate value by question id
	public boolean getQuestionIsPrivate(int questionId) throws NotFoundException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);
		return question.getIsPrivate();
	}

	// Return question authorId value by question id
	public int getQuestionAuthorId(int questionId) throws NotFoundException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);
		return question.getAuthorId();
	}
	
	// Return a list of user IDs that asked questions
	public List<Integer> getUserIdList() {
		return questionRepository.findAllAuthorId();
	}

}

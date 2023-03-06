package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.exception.CategoryNotExistException;
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
	protected RestTemplate restTemplate = new RestTemplate();
	
	protected static final String PUBLIC = "public";
	protected static final String PRIVATE = "private";
	protected static final String ALL = "all";
	
	// Add a new question
	public void createQuestion(QuestionRequest questionRequest) throws CategoryNotExistException {
		if (!isCategoryExists(questionRequest.getCategoryId())) {
			throw new CategoryNotExistException("Category doesn't exist");
		}

		int currentUserId = Utils.getCurrentUserId();
		LocalDateTime dateTime = LocalDateTime.now();
		QuestionDTO questionDTO = QuestionDTO.builder()
				.content(questionRequest.getContent())
				.categoryId(questionRequest.getCategoryId())
		    	.isPrivate(questionRequest.getIsPrivate())
		    	.authorId(currentUserId)
		    	.date(dateTime)
		    	.build();
		Question question = questionMapper.toQuestion(questionDTO);
		questionRepository.save(question);
	}
	
	// Return list of subcategory id
	public List<Integer> getAllSubcategoryId(int parentId) {
	    String url = "http://localhost:8765/external/categories/subcategoriesId/" + parentId;
	    ResponseEntity<List<Integer>> response = restTemplate.exchange(
	    	url,
	    	HttpMethod.GET,
	    	null,
	    	new ParameterizedTypeReference<List<Integer>>() {}
	    );
	    return response.getBody();
	}
	
	// Return is category exists
	public boolean isCategoryExists(int categoryId) {
		String url = "http://localhost:8765/external/categories/exists/" + categoryId;
		ResponseEntity<Boolean> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Boolean.class
		);
		return response.getBody();
	}
	
	// Return list of question id by category
	public List<Integer> getAllQuestionIdByCategory(int categoryId) {
		return questionRepository.findAllQuestionIdByCategory(categoryId);
	}
	
}
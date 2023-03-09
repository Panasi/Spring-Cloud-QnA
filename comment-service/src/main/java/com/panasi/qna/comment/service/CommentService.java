package com.panasi.qna.comment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.comment.dto.AnswerCommentDTO;
import com.panasi.qna.comment.dto.QuestionCommentDTO;
import com.panasi.qna.comment.mapper.AnswerCommentMapper;
import com.panasi.qna.comment.mapper.QuestionCommentMapper;
import com.panasi.qna.comment.repository.AnswerCommentRepository;
import com.panasi.qna.comment.repository.QuestionCommentRepository;

@Service
public class CommentService {
	
	@Autowired
	protected QuestionCommentRepository questionCommentRepository;
	@Autowired
	protected AnswerCommentRepository answerCommentRepository;
	@Autowired
	protected QuestionCommentMapper questionCommentMapper;
	@Autowired
	protected AnswerCommentMapper answerCommentMapper;
	protected RestTemplate restTemplate = new RestTemplate();
	
	// Sort question comments
	public List<QuestionCommentDTO> sortQuestionCommentDTOs(List<QuestionCommentDTO> questionCommentDTOs) {
		List<QuestionCommentDTO> sortedComments = new ArrayList<>(questionCommentDTOs);
		sortedComments.sort((q1,q2) -> {
			int compare = q1.getQuestionId().compareTo(q2.getQuestionId());
			if (compare == 0) {
				compare = q1.getDate().compareTo(q2.getDate());
			}
			return compare;
			});
		return sortedComments;
	}
	
	// Sort answer comments
	public List<AnswerCommentDTO> sortAnswerCommentDTOs(List<AnswerCommentDTO> answerCommentDTOs) {
		List<AnswerCommentDTO> sortedComments = new ArrayList<>(answerCommentDTOs);
		sortedComments.sort((q1,q2) -> {
			int compare = q1.getAnswerId().compareTo(q2.getAnswerId());
			if (compare == 0) {
				compare = q1.getDate().compareTo(q2.getDate());
			}
			return compare;
		});
		return sortedComments;
	}
	
	// Return is question exists
	public boolean isQuestionExists(int questionId) {
		String url = "http://localhost:8765/external/questions/exists/" + questionId;
		ResponseEntity<Boolean> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Boolean.class
		);
		return response.getBody();
	}
	
	// Return is answer exists
	public boolean isAnswerExists(int answerId) {
		String url = "http://localhost:8765/external/answers/exists/" + answerId;
		ResponseEntity<Boolean> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Boolean.class
		);
		return response.getBody();
	}
	
	// Return question isPrivate value by question id
	public boolean getQuestionIsPrivate(int questionId) {
		String url = "http://localhost:8765/external/questions/isPrivate/" + questionId;
		ResponseEntity<Boolean> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Boolean.class
		);
		return response.getBody();
	}
	
	// Return answer isPrivate value by answer id
	public boolean getAnswerIsPrivate(int answerId) {
		String url = "http://localhost:8765/external/answers/isPrivate/" + answerId;
		ResponseEntity<Boolean> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Boolean.class
		);
		return response.getBody();
	}
	
	// Return question authorId value by question id
	public int getQuestionAuthorId(int questionId) {
		String url = "http://localhost:8765/external/questions/authorId/" + questionId;
		ResponseEntity<Integer> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Integer.class
		);
		return response.getBody();
	}
	
	// Return answer authorId value by answer id
	public int getAnswerAuthorId(int answerId) {
		String url = "http://localhost:8765/external/answers/authorId/" + answerId;
		ResponseEntity<Integer> response = restTemplate.exchange(
			url,
			HttpMethod.GET,
			null,
			Integer.class
		);
		return response.getBody();
	}
	
	// Return question rating
	public Double getQuestionRating(int questionId) {
		Double rating = questionCommentRepository.getRating(questionId);
	    return rating != null ? rating : null;
	}
	
	// Return answer rating
	public Double getAnswerRating(int answerId) {
		Double rating = answerCommentRepository.getRating(answerId);
		return rating != null ? rating : null;
	}

}

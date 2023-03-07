package com.panasi.qna.answer.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.entity.Answer;
import com.panasi.qna.answer.exception.QuestionNotExistException;
import com.panasi.qna.answer.mapper.AnswerMapper;
import com.panasi.qna.answer.payload.AnswerRequest;
import com.panasi.qna.answer.payload.Utils;
import com.panasi.qna.answer.repository.AnswerRepository;

@Service
public class AnswerService {
	
	@Autowired
	protected AnswerRepository answerRepository;
	@Autowired
	protected AnswerMapper answerMapper;
	protected RestTemplate restTemplate = new RestTemplate();
	
	protected static final String PUBLIC = "public";
	protected static final String PRIVATE = "private";
	protected static final String ALL = "all";
	
	// Add a new answer
	public void createAnswer(AnswerRequest answerRequest) throws QuestionNotExistException {
		if (!isQuestionExists(answerRequest.getQuestionId())) {
			throw new QuestionNotExistException("Question doesn't exist");
		}
		
		int currentUserId = Utils.getCurrentUserId();
		LocalDateTime dateTime = LocalDateTime.now();
		AnswerDTO answerDto = AnswerDTO.builder()
			.content(answerRequest.getContent())
			.questionId(answerRequest.getQuestionId())
			.isPrivate(answerRequest.getIsPrivate())
			.authorId(currentUserId)
			.date(dateTime)
			.build();
		Answer answer = answerMapper.toAnswer(answerDto);
		answerRepository.save(answer);
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

}

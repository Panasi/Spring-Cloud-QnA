package com.panasi.qna.answer.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.entity.Answer;
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
	public void createAnswer(AnswerRequest answerRequest) throws NotFoundException {
		if (!isQuestionExists(answerRequest.getQuestionId())) {
			throw new NotFoundException();
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
	
	// Return all answers by question
	public List<AnswerDTO> getAnswersByQuestion(int questionId) {
		List<Answer> answerDTOs = answerRepository.findAllByQuestionId(questionId);
		return answerMapper.toAnswerDTOs(answerDTOs);
	}
	
	// Return all answers by question and author
	public List<AnswerDTO> getAnswersByQuestionAndAuthor(int questionId, int authorId) {
		List<Answer> answerDTOs = answerRepository.findAllByQuestionIdandAuthorId(questionId, authorId);
		return answerMapper.toAnswerDTOs(answerDTOs);
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
		return answerRepository.existsById(answerId);
	}
	
	// Return answer isPrivate value by answer id
	public boolean getAnswerIsPrivate(int answerId) {
		return answerRepository.findIsPrivateById(answerId);
	}
		
	// Return answer authorId value by answer id
	public int getAnswerAuthorId(int answerId) {
		return answerRepository.findAuthorIdById(answerId);
	}

}

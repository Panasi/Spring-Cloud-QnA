package com.panasi.qna.answer.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.entity.Answer;
import com.panasi.qna.answer.payload.AnswerRequest;

@Service
public class AdminAnswerService extends AnswerService {
	
	// Return all answers
	public List<AnswerDTO> getAllAnswers(String access) {
		List<Answer> answers;
		if (access.equals(PUBLIC)) {
			answers = answerRepository.findAllByIsPrivate(false);
		} else if (access.equals(PRIVATE)) {
			answers = answerRepository.findAllByIsPrivate(true);
		} else {
			answers = answerRepository.findAll();
		}
		return answerMapper.toAnswerDTOs(answers);
	}
	
	// Return all user answers
	public List<AnswerDTO> getAllUserAnswers(int authorId, String access) {
		List<Answer> answers;
		if (access.equals(PUBLIC)) {
			answers = answerRepository.findAllByAuthorIdAndIsPrivate(authorId, false);
		} else if (access.equals(PRIVATE)) {
			answers = answerRepository.findAllByAuthorIdAndIsPrivate(authorId, true);
		} else {
			answers = answerRepository.findAllByAuthorId(authorId);
		}
		return answerMapper.toAnswerDTOs(answers);
	}
	
	// Return answer by id
	public AnswerDTO getAnswerById(int answerId) throws NotFoundException {
		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(NotFoundException::new);
		return answerMapper.toAnswerDTO(answer);
	}
	
	// Update certain answer
	public void updateAnswer(AnswerRequest answerRequest, int answerId) throws NotFoundException {
		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(NotFoundException::new);
		LocalDateTime dateTime = LocalDateTime.now();
		answer.setDate(dateTime);
		if (Objects.nonNull(answerRequest.getContent())) {
			answer.setContent(answerRequest.getContent());
		}
		if (Objects.nonNull(answerRequest.getQuestionId())) {
			answer.setQuestionId(answerRequest.getQuestionId());
		}
		if (Objects.nonNull(answerRequest.getIsPrivate())) {
			answer.setIsPrivate(answerRequest.getIsPrivate());
		}
		answerRepository.save(answer);
	}
	
	// Delete certain answer
	public void deleteAnswer(int answerId) {
		answerRepository.deleteById(answerId);
	}

}

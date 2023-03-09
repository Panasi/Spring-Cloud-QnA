package com.panasi.qna.answer.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.entity.Answer;
import com.panasi.qna.answer.exception.ForbiddenException;
import com.panasi.qna.answer.payload.AnswerRequest;
import com.panasi.qna.answer.payload.Utils;

@Service
public class UserAnswerService extends AnswerService {
	
	// Return user answers
	public List<AnswerDTO> getUserAnswers(int authorId, String access) {
		int currentUserId = Utils.getCurrentUserId();
		List<Answer> answers;
		
		if (access.equals(PRIVATE) && authorId != currentUserId) {
	        return new ArrayList<>();
	    }

	    if (access.equals(PUBLIC) || (access.equals(ALL) && authorId != currentUserId)) {
	    	answers = answerRepository.findAllByAuthorIdAndIsPrivate(authorId, false);
	    } else if (access.equals(PRIVATE)) {
	    	answers = answerRepository.findAllByAuthorIdAndIsPrivate(authorId, true);
	    } else {
	    	answers = answerRepository.findAllByAuthorId(authorId);
	    }
	    
		return answerMapper.toAnswerDTOs(answers);
	}
	
	// Return answer by id
	public AnswerDTO getAnswerById(int answerId) throws NotFoundException, ForbiddenException {
		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(NotFoundException::new);
		
		boolean isAnswerPrivate = answer.getIsPrivate();
		int answerAuthorId = answer.getAuthorId();
		int currentUserId = Utils.getCurrentUserId();
		
		if (isAnswerPrivate && answerAuthorId != currentUserId) {
			throw new ForbiddenException("You can't get another private answer");
		}
		
		return answerMapper.toAnswerDTO(answer);
	}
	
	// Update certain answer
	public void updateAnswer(AnswerRequest answerRequest, int answerId) throws NotFoundException, ForbiddenException {
		int currentUserId = Utils.getCurrentUserId();
		Answer answer = answerRepository.findById(answerId)
				.orElseThrow(NotFoundException::new);
		
		if (answer.getAuthorId() != currentUserId) {
	    	throw new ForbiddenException("You can't update another users answers");
	    }
		
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

}

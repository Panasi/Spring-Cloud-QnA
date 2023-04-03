package com.panasi.qna.answer.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

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
	@Autowired
	protected RabbitTemplate rabbitTemplate;

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
		AnswerDTO answerDto = AnswerDTO.builder().content(answerRequest.getContent())
				.questionId(answerRequest.getQuestionId()).isPrivate(answerRequest.getIsPrivate())
				.authorId(currentUserId).date(dateTime).build();
		Answer answer = answerMapper.toAnswer(answerDto);
		answerRepository.save(answer);
	}

	// Sort Answers
	public List<AnswerDTO> sortAnswersDTO(List<AnswerDTO> answersDTO) {
		List<AnswerDTO> sortedAnswersDTO = new ArrayList<>(answersDTO);
		sortedAnswersDTO.sort((q1, q2) -> {
			int compare = q1.getQuestionId().compareTo(q2.getQuestionId());
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
		return sortedAnswersDTO;
	}

	// Return is question exists from question service
	public boolean isQuestionExists(int questionId) {
		return (Boolean) rabbitTemplate.convertSendAndReceive("isQuestionExistsQueue", questionId);
	}

	// Return answer rating from comment service
	public Double getRating(int answerId) {
		Double rating = (Double) rabbitTemplate.convertSendAndReceive("getAnswerRatingQueue", answerId);
		return rating != 0.0 ? (Math.ceil(rating * 100) / 100) : null;
	}

	// Return all answers by questionId to question service
	@RabbitListener(queues = "getAnswersByQuestionQueue")
	public List<AnswerDTO> getAnswersByQuestion(int questionId) {
		List<Answer> answers = answerRepository.findAllByQuestionId(questionId);
		List<AnswerDTO> answersDTO = answerMapper.toAnswerDTOs(answers);
		answersDTO.forEach(answerDTO -> answerDTO.setRating(getRating(answerDTO.getId())));
		return sortAnswersDTO(answersDTO);
	}

	// Return all answers by questionId and authorId
	@RabbitListener(queues = "getAnswersByQuestionAndAuthorQueue")
	public List<AnswerDTO> getAnswersByQuestionAndAuthor(List<Integer> params) {
		int questionId = params.get(0);
		int authorId = params.get(1);
		List<Answer> answers = answerRepository.findAllByQuestionIdandAuthorId(questionId, authorId);
		List<AnswerDTO> answersDTO = answerMapper.toAnswerDTOs(answers);
		answersDTO.forEach(answerDTO -> answerDTO.setRating(getRating(answerDTO.getId())));
		return sortAnswersDTO(answersDTO);
	}

	// Return is answer exists
	@RabbitListener(queues = "isAnswerExistsQueue")
	public boolean isAnswerExists(int answerId) {
		return answerRepository.existsById(answerId);
	}

	// Return answer isPrivate value by answer id
	@RabbitListener(queues = "getAnswerIsPrivateQueue")
	public boolean getAnswerIsPrivate(int answerId) throws NotFoundException {
		Answer answer = answerRepository.findById(answerId).orElseThrow(NotFoundException::new);
		return answer.getIsPrivate();
	}

	// Return answer authorId value by answer id
	@RabbitListener(queues = "getAnswerAuthorIdQueue")
	public int getAnswerAuthorId(int answerId) throws NotFoundException {
		Answer answer = answerRepository.findById(answerId).orElseThrow(NotFoundException::new);
		return answer.getAuthorId();
	}

}

package com.panasi.qna.comment.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	@Autowired
	protected RabbitTemplate rabbitTemplate;

	// Sort question comments
	public List<QuestionCommentDTO> sortQuestionCommentDTOs(List<QuestionCommentDTO> questionCommentDTOs) {
		List<QuestionCommentDTO> sortedComments = new ArrayList<>(questionCommentDTOs);
		sortedComments.sort((q1, q2) -> {
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
		sortedComments.sort((q1, q2) -> {
			int compare = q1.getAnswerId().compareTo(q2.getAnswerId());
			if (compare == 0) {
				compare = q1.getDate().compareTo(q2.getDate());
			}
			return compare;
		});
		return sortedComments;
	}

	// Return is question exists from question service
	public boolean isQuestionExists(int questionId) {
		return (Boolean) rabbitTemplate.convertSendAndReceive("isQuestionExistsQueue", questionId);
	}

	// Return is answer exists from answer service
	public boolean isAnswerExists(int answerId) {
		return (Boolean) rabbitTemplate.convertSendAndReceive("isAnswerExistsQueue", answerId);
	}

	// Return question isPrivate value by question id from question service
	public boolean getQuestionIsPrivate(int questionId) {
		return (Boolean) rabbitTemplate.convertSendAndReceive("getQuestionIsPrivateQueue", questionId);
	}

	// Return answer isPrivate value by answer id from answer service
	public boolean getAnswerIsPrivate(int answerId) {
		return (Boolean) rabbitTemplate.convertSendAndReceive("getAnswerIsPrivateQueue", answerId);
	}

	// Return question authorId value by question id from question service
	public int getQuestionAuthorId(int questionId) {
		return (Integer) rabbitTemplate.convertSendAndReceive("getQuestionAuthorIdQueue", questionId);
	}

	// Return answer authorId value by answer id from answer service
	public int getAnswerAuthorId(int answerId) {
		return (Integer) rabbitTemplate.convertSendAndReceive("getAnswerAuthorIdQueue", answerId);
	}

	// Return question rating
	@RabbitListener(queues = "getQuestionRatingQueue")
	public Double getQuestionRating(int questionId) {
		Double rating = questionCommentRepository.getRating(questionId);
		return rating != null ? rating : 0.0;
	}

	// Return answer rating
	@RabbitListener(queues = "getAnswerRatingQueue")
	public Double getAnswerRating(int answerId) {
		Double rating = answerCommentRepository.getRating(answerId);
		return rating != null ? rating : 0.0;
	}

}

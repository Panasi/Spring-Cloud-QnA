package com.panasi.qna.comment.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panasi.qna.comment.dto.AnswerCommentDTO;
import com.panasi.qna.comment.dto.QuestionCommentDTO;
import com.panasi.qna.comment.entity.AnswerComment;
import com.panasi.qna.comment.entity.QuestionComment;
import com.panasi.qna.comment.exception.ForbiddenException;
import com.panasi.qna.comment.payload.CommentInput;
import com.panasi.qna.comment.payload.Utils;

@Service
public class UserCommentService extends CommentService {

	// Return all comments to question
	public List<QuestionCommentDTO> getAllCommentsToQuestion(int questionId)
			throws NotFoundException, ForbiddenException {
		if (!isQuestionExists(questionId)) {
			throw new NotFoundException();
		}

		boolean isQuestionPrivate = getQuestionIsPrivate(questionId);
		int questionAuthorId = getQuestionAuthorId(questionId);
		int currentUserId = Utils.getCurrentUserId();

		if (isQuestionPrivate && questionAuthorId != currentUserId) {
			throw new ForbiddenException("Can't get comments on another user's private questions");
		}

		List<QuestionComment> allComments = questionCommentRepository.findAllByQuestionId(questionId);
		List<QuestionCommentDTO> allCommentDTOs = questionCommentMapper.toCommentDTOs(allComments);
		return sortQuestionCommentDTOs(allCommentDTOs);
	}

	// Return all comments to answer
	public List<AnswerCommentDTO> getAllCommentsToAnswer(int answerId) throws NotFoundException, ForbiddenException {
		if (!isAnswerExists(answerId)) {
			throw new NotFoundException();
		}

		boolean isAnswerPrivate = getAnswerIsPrivate(answerId);
		int answerAuthorId = getAnswerAuthorId(answerId);
		int currentUserId = Utils.getCurrentUserId();

		if (isAnswerPrivate && answerAuthorId != currentUserId) {
			throw new ForbiddenException("Can't get comments on another user's private answers");
		}

		List<AnswerComment> allComments = answerCommentRepository.findAllByAnswerId(answerId);
		List<AnswerCommentDTO> allCommentDTOs = answerCommentMapper.toCommentDTOs(allComments);
		return sortAnswerCommentDTOs(allCommentDTOs);
	}

	// Return question comment by id
	public QuestionCommentDTO getQuestionCommentById(int commentId) throws NotFoundException, ForbiddenException {

		QuestionComment comment = questionCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);

		int questionId = comment.getQuestionId();
		boolean isQuestionPrivate = getQuestionIsPrivate(questionId);
		int questionAuthorId = getQuestionAuthorId(questionId);
		int currentUserId = Utils.getCurrentUserId();

		if (isQuestionPrivate && questionAuthorId != currentUserId) {
			throw new ForbiddenException("Can't get comment on another user's private question");
		}

		return questionCommentMapper.toCommentDTO(comment);
	}

	// Return answer comment by id
	public AnswerCommentDTO getAnswerCommentById(int commentId) throws NotFoundException, ForbiddenException {
		AnswerComment comment = answerCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);

		int answerId = comment.getAnswerId();
		boolean isAnswerPrivate = getAnswerIsPrivate(answerId);
		int answerAuthorId = getAnswerAuthorId(answerId);
		int currentUserId = Utils.getCurrentUserId();

		if (isAnswerPrivate && answerAuthorId != currentUserId) {
			throw new ForbiddenException("Can't get comment on another user's private answer");
		}

		return answerCommentMapper.toCommentDTO(comment);
	}

	// Add a new comment to question
	public void createQuestionComment(CommentInput commentRequest) throws NotFoundException, ForbiddenException {
		int questionId = commentRequest.getTargetId();
		if (!isQuestionExists(questionId)) {
			throw new NotFoundException();
		}

		boolean isQuestionPrivate = getQuestionIsPrivate(questionId);
		int questionAuthorId = getQuestionAuthorId(questionId);
		int currentUserId = Utils.getCurrentUserId();

		if (isQuestionPrivate && questionAuthorId != currentUserId) {
			throw new ForbiddenException("Can't comment on another user's private questions");
		}

		LocalDateTime dateTime = LocalDateTime.now();
		QuestionCommentDTO commentDTO = QuestionCommentDTO.builder().questionId(questionId)
				.content(commentRequest.getContent()).rate(commentRequest.getRate()).authorId(currentUserId)
				.date(dateTime).build();
		QuestionComment comment = questionCommentMapper.toComment(commentDTO);
		questionCommentRepository.save(comment);
	}

	// Add a new comment to answer
	public void createAnswerComment(CommentInput commentRequest) throws NotFoundException, ForbiddenException {
		int answerId = commentRequest.getTargetId();
		if (!isAnswerExists(answerId)) {
			throw new NotFoundException();
		}

		boolean isAnswerPrivate = getAnswerIsPrivate(answerId);
		int answerAuthorId = getAnswerAuthorId(answerId);
		int currentUserId = Utils.getCurrentUserId();

		if (isAnswerPrivate && answerAuthorId != currentUserId) {
			throw new ForbiddenException("Can't comment on another user's private answers");
		}

		LocalDateTime dateTime = LocalDateTime.now();
		AnswerCommentDTO commentDTO = AnswerCommentDTO.builder().answerId(answerId).content(commentRequest.getContent())
				.rate(commentRequest.getRate()).authorId(currentUserId).date(dateTime).build();
		AnswerComment comment = answerCommentMapper.toComment(commentDTO);
		answerCommentRepository.save(comment);

	}

	// Update question comment
	@Transactional
	public void updateQuestionComment(CommentInput commentRequest, int commentId)
			throws NotFoundException, ForbiddenException {
		QuestionComment comment = questionCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);

		int currentUserId = Utils.getCurrentUserId();

		if (currentUserId != comment.getAuthorId()) {
			throw new ForbiddenException("Can't update another user's questions");
		}

		LocalDateTime dateTime = LocalDateTime.now();
		comment.setDate(dateTime);
		if (Objects.nonNull(commentRequest.getContent())) {
			comment.setContent(commentRequest.getContent());
		}
		if (Objects.nonNull(commentRequest.getRate())) {
			comment.setRate(commentRequest.getRate());
		}
		questionCommentRepository.save(comment);
	}

	// Update answer comment
	@Transactional
	public void updateAnswerComment(CommentInput commentRequest, int commentId)
			throws NotFoundException, ForbiddenException {
		AnswerComment comment = answerCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);

		int currentUserId = Utils.getCurrentUserId();

		if (currentUserId != comment.getAuthorId()) {
			throw new ForbiddenException("Can't update another user's answers");
		}
		LocalDateTime dateTime = LocalDateTime.now();
		comment.setDate(dateTime);
		if (Objects.nonNull(commentRequest.getContent())) {
			comment.setContent(commentRequest.getContent());
		}
		if (Objects.nonNull(commentRequest.getRate())) {
			comment.setRate(commentRequest.getRate());
		}
		answerCommentRepository.save(comment);
	}

}

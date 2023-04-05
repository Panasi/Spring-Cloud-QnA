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
import com.panasi.qna.comment.payload.CommentInput;
import com.panasi.qna.comment.payload.Utils;

@Service
public class AdminCommentService extends CommentService {

	// Return all comments to question
	public List<QuestionCommentDTO> getAllCommentsToQuestion(int questionId) {
		List<QuestionComment> allComments = questionCommentRepository.findAllByQuestionId(questionId);
		List<QuestionCommentDTO> allCommentDTOs = questionCommentMapper.toCommentDTOs(allComments);
		return sortQuestionCommentDTOs(allCommentDTOs);
	}

	// Return all comments to answer
	public List<AnswerCommentDTO> getAllCommentsToAnswer(int answerId) {
		List<AnswerComment> allComments = answerCommentRepository.findAllByAnswerId(answerId);
		List<AnswerCommentDTO> allCommentDTOs = answerCommentMapper.toCommentDTOs(allComments);
		return sortAnswerCommentDTOs(allCommentDTOs);
	}

	// Return all author comments to all questions
	public List<QuestionCommentDTO> getAllUserCommentsToQuestions(int authorId) {
		List<QuestionComment> allUserComments = questionCommentRepository.findAllByAuthorId(authorId);
		List<QuestionCommentDTO> allUserCommentDTOs = questionCommentMapper.toCommentDTOs(allUserComments);
		return sortQuestionCommentDTOs(allUserCommentDTOs);
	}

	// Return all author comments to all answers
	public List<AnswerCommentDTO> getAllUserCommentsToAnswers(int authorId) {
		List<AnswerComment> allUserComments = answerCommentRepository.findAllByAuthorId(authorId);
		List<AnswerCommentDTO> allUserCommentDTOs = answerCommentMapper.toCommentDTOs(allUserComments);
		return sortAnswerCommentDTOs(allUserCommentDTOs);
	}

	// Return question comment by id
	public QuestionCommentDTO getQuestionCommentById(int commentId) throws NotFoundException {
		QuestionComment comment = questionCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);
		return questionCommentMapper.toCommentDTO(comment);
	}

	// Return answer comment by id
	public AnswerCommentDTO getAnswerCommentById(int commentId) throws NotFoundException {
		AnswerComment comment = answerCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);
		return answerCommentMapper.toCommentDTO(comment);
	}

	// Add a new comment to question
	public void createQuestionComment(CommentInput commentRequest) throws NotFoundException {
		int questionId = commentRequest.getTargetId();
		if (!isQuestionExists(questionId)) {
			throw new NotFoundException();
		}

		int currentUserId = Utils.getCurrentUserId();
		LocalDateTime dateTime = LocalDateTime.now();
		QuestionCommentDTO commentDTO = QuestionCommentDTO.builder().questionId(questionId)
				.content(commentRequest.getContent()).rate(commentRequest.getRate()).authorId(currentUserId)
				.date(dateTime).build();
		QuestionComment comment = questionCommentMapper.toComment(commentDTO);
		questionCommentRepository.save(comment);
	}

	// Add a new comment to answer
	public void createAnswerComment(CommentInput commentRequest) throws NotFoundException {
		int answerId = commentRequest.getTargetId();
		if (!isAnswerExists(answerId)) {
			throw new NotFoundException();
		}

		int currentUserId = Utils.getCurrentUserId();
		LocalDateTime dateTime = LocalDateTime.now();
		AnswerCommentDTO commentDTO = AnswerCommentDTO.builder().answerId(answerId).content(commentRequest.getContent())
				.rate(commentRequest.getRate()).authorId(currentUserId).date(dateTime).build();
		AnswerComment comment = answerCommentMapper.toComment(commentDTO);
		answerCommentRepository.save(comment);
	}

	// Update question comment
	@Transactional
	public void updateQuestionComment(CommentInput commentRequest, int commentId) throws NotFoundException {
		QuestionComment comment = questionCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);
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
	public void updateAnswerComment(CommentInput commentRequest, int commentId) throws NotFoundException {
		AnswerComment comment = answerCommentRepository.findById(commentId).orElseThrow(NotFoundException::new);
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

	// Delete question comment
	public void deleteQuestionComment(int commentId) {
		questionCommentRepository.deleteById(commentId);
	}

	// Delete answer comment
	public void deleteAnswerComment(int commentId) {
		answerCommentRepository.deleteById(commentId);
	}

}

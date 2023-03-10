package com.panasi.qna.comment.controller;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.comment.dto.AnswerCommentDTO;
import com.panasi.qna.comment.dto.QuestionCommentDTO;
import com.panasi.qna.comment.exception.ForbiddenException;
import com.panasi.qna.comment.payload.CommentRequest;
import com.panasi.qna.comment.service.UserCommentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class UserCommentController {

	private final UserCommentService service;

	@GetMapping("/question/{questionId}/all")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Get all comments to the question")
	public ResponseEntity<List<QuestionCommentDTO>> getAllCommentsToQuestion(@PathVariable int questionId)
			throws NotFoundException, ForbiddenException {
		List<QuestionCommentDTO> allCommentDTOs = service.getAllCommentsToQuestion(questionId);
		return new ResponseEntity<>(allCommentDTOs, HttpStatus.OK);
	}

	@GetMapping("/answer/{answerId}/all")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Get all comments to the answer")
	public ResponseEntity<List<AnswerCommentDTO>> getAllCommentsToAnswer(@PathVariable int answerId)
			throws NotFoundException, ForbiddenException {
		List<AnswerCommentDTO> allCommentDTOs = service.getAllCommentsToAnswer(answerId);
		return new ResponseEntity<>(allCommentDTOs, HttpStatus.OK);
	}

	@GetMapping("/questions/comment/{id}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Get question comment by id")
	public ResponseEntity<QuestionCommentDTO> getQuestionCommentById(@PathVariable int id)
			throws NotFoundException, ForbiddenException {
		QuestionCommentDTO commentDTO = service.getQuestionCommentById(id);
		return new ResponseEntity<>(commentDTO, HttpStatus.OK);
	}

	@GetMapping("/answers/comment/{id}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Get answer comment by id")
	public ResponseEntity<AnswerCommentDTO> getAnswerCommentById(@PathVariable int id)
			throws NotFoundException, ForbiddenException {
		AnswerCommentDTO commentDTO = service.getAnswerCommentById(id);
		return new ResponseEntity<>(commentDTO, HttpStatus.OK);
	}

	@PostMapping("/question/{questionId}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Add a new question comment")
	public ResponseEntity<CommentRequest> addNewQuestionComment(@RequestBody CommentRequest commentRequest,
			@PathVariable int questionId) throws NotFoundException, ForbiddenException {
		service.createQuestionComment(commentRequest, questionId);
		return new ResponseEntity<>(commentRequest, HttpStatus.CREATED);
	}

	@PostMapping("/answer/{answerId}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Add a new answer comment")
	public ResponseEntity<CommentRequest> addNewAnswerComment(@RequestBody CommentRequest commentRequest,
			@PathVariable int answerId) throws NotFoundException, ForbiddenException {
		service.createAnswerComment(commentRequest, answerId);
		return new ResponseEntity<>(commentRequest, HttpStatus.CREATED);
	}

	@PutMapping("/questions/comment/{id}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Update question comment")
	public ResponseEntity<CommentRequest> updateQuestionComment(@RequestBody CommentRequest commentRequest,
			@PathVariable int id) throws NotFoundException, ForbiddenException {
		service.updateQuestionComment(commentRequest, id);
		return new ResponseEntity<>(commentRequest, HttpStatus.ACCEPTED);
	}

	@PutMapping("/answers/comment/{id}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Update answer comment")
	public ResponseEntity<CommentRequest> updateAnswerComment(@RequestBody CommentRequest commentRequest,
			@PathVariable int id) throws NotFoundException, ForbiddenException {
		service.updateAnswerComment(commentRequest, id);
		return new ResponseEntity<>(commentRequest, HttpStatus.ACCEPTED);
	}

}

package com.panasi.qna.comment.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.comment.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/external/comments")
public class ExternalCommentController {

	private final CommentService commentService;

	@GetMapping("/question/rating/{questionId}")
	@Operation(summary = "Get question rating")
	public Double getQuestionRating(@PathVariable int questionId) {
		return commentService.getQuestionRating(questionId);
	}

	@GetMapping("/answer/rating/{answerId}")
	@Operation(summary = "Get answer rating")
	public Double getAnswerRating(@PathVariable int answerId) {
		return commentService.getAnswerRating(answerId);
	}

}

package com.panasi.qna.answer.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.service.AnswerService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/external/answers")
public class ExternalAnswerController {
	
	private final AnswerService answerService;
	
	@GetMapping("/exists/{id}")
	@Operation(summary = "Is answer exists")
	public boolean isAnswerExists(@PathVariable int id) {
		return answerService.isAnswerExists(id);
	}
	
	@GetMapping("/isPrivate/{id}")
	@Operation(summary = "Get answer isPrivate by answer id")
	public boolean getAnswerIsPrivate(@PathVariable int id) {
		return answerService.getAnswerIsPrivate(id);
	}
	
	@GetMapping("/authorId/{id}")
	@Operation(summary = "Get answer authorId by answer id")
	public boolean getAnswerAuthorId(@PathVariable int id) {
		return answerService.getAnswerIsPrivate(id);
	}
	
	@GetMapping("/question")
	@Operation(summary = "Get list of answers by question")
	public List<AnswerDTO> getAnswersByQuestion(
			@RequestParam int questionId,
			@RequestParam(required = false) Integer authorId) {
	    if (authorId == null) {
	        return answerService.getAnswersByQuestion(questionId);
	    }
	    return answerService.getAnswersByQuestionAndAuthor(questionId, authorId);
	}

}

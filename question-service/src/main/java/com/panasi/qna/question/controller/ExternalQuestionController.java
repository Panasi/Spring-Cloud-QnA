package com.panasi.qna.question.controller;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.question.service.QuestionService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/external/questions")
public class ExternalQuestionController {

	private final QuestionService questionService;

	@GetMapping("/categoryId/{id}")
	@Operation(summary = "Get all question id from category")
	public List<Integer> getQuestionsIdFromCategory(@PathVariable int id) {
		return questionService.getAllQuestionIdByCategory(id);
	}

	@GetMapping("/exists/{id}")
	@Operation(summary = "Is question exists")
	public boolean isQuestionExists(@PathVariable int id) {
		return questionService.isQuestionExists(id);
	}

	@GetMapping("/isPrivate/{id}")
	@Operation(summary = "Get question isPrivate by question id")
	public boolean getQuestionIsPrivate(@PathVariable int id) throws NotFoundException {
		return questionService.getQuestionIsPrivate(id);
	}

	@GetMapping("/authorId/{id}")
	@Operation(summary = "Get question authorId by question id")
	public boolean getQuestionAuthorId(@PathVariable int id) throws NotFoundException {
		return questionService.getQuestionIsPrivate(id);
	}
	
	@GetMapping("/authors")
	@Operation(summary = "Get question authorId id list")
	public List<Integer> getAuthorIdList() {
		return questionService.getUserIdList();
	}

}

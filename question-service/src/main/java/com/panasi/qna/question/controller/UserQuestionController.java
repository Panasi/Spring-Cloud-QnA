package com.panasi.qna.question.controller;

import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.exception.ForbiddenException;
import com.panasi.qna.question.payload.QuestionRequest;
import com.panasi.qna.question.service.UserQuestionService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/questions")
public class UserQuestionController {

	private final UserQuestionService service;

	@GetMapping("/category/{categoryId}")
	@Operation(summary = "Get questions from certain category")
	public ResponseEntity<List<QuestionDTO>> getQuestionsFromCategory(@PathVariable int categoryId,
			@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getCategoryQuestions(categoryId, access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/subcategory/{categoryId}")
	@Operation(summary = "Get questions from certain category and all its subcategories")
	public ResponseEntity<List<QuestionDTO>> getQuestionsFromSubcategories(@PathVariable int categoryId,
			@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> result = new ArrayList<>();
		List<QuestionDTO> allSubQuestionDtos = service.getSubcategoriesQuestions(categoryId, access, result);
		return new ResponseEntity<>(allSubQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/user/{authorId}")
	@Operation(summary = "Get user questions")
	public ResponseEntity<List<QuestionDTO>> getAllPrivateQuestions(@PathVariable int authorId,
			@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getUserQuestions(authorId, access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/{questionId}")
	@Operation(summary = "Get question with answers by id")
	public ResponseEntity<QuestionWithAnswersDTO> getQuestion(@PathVariable int questionId)
			throws NotFoundException, ForbiddenException {
		QuestionWithAnswersDTO questionDTO = service.getQuestionById(questionId);
		return new ResponseEntity<>(questionDTO, HttpStatus.OK);
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Add a new question")
	public ResponseEntity<QuestionRequest> addNewQuestion(@RequestBody QuestionRequest questionRequest)
			throws NotFoundException {
		service.createQuestion(questionRequest);
		return new ResponseEntity<>(questionRequest, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Update question")
	public ResponseEntity<QuestionRequest> updateQuestion(@RequestBody QuestionRequest questionRequest,
			@PathVariable int id) throws NotFoundException, ForbiddenException {
		service.updateQuestion(questionRequest, id);
		return new ResponseEntity<>(questionRequest, HttpStatus.ACCEPTED);
	}

}

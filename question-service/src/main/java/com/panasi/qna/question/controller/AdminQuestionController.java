package com.panasi.qna.question.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.panasi.qna.question.payload.MessageResponse;
import com.panasi.qna.question.payload.QuestionInput;
import com.panasi.qna.question.service.AdminQuestionService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/questions")
public class AdminQuestionController {

	private final AdminQuestionService service;

	@GetMapping()
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all questions")
	public ResponseEntity<List<QuestionDTO>> getAllQuestions(@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getAllQuestions(access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/category/{categoryId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get questions from certain category")
	public ResponseEntity<List<QuestionDTO>> getQuestionsFromCategory(@PathVariable int categoryId,
			@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getCategoryQuestions(categoryId, access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/subcategory/{categoryId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get questions from certain category and all its subcategories")
	public ResponseEntity<List<QuestionDTO>> getQuestionsFromSubcategories(@PathVariable int categoryId,
			@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> result = new ArrayList<>();
		List<QuestionDTO> allSubQuestionDtos = service.getSubcategoriesQuestions(categoryId, access, result);
		return new ResponseEntity<>(allSubQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/user/{authorId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all user questions")
	public ResponseEntity<List<QuestionDTO>> getAllUserQuestions(@PathVariable int authorId,
			@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getUserQuestions(authorId, access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}

	@GetMapping("/{questionId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get question with answers by id")
	public ResponseEntity<QuestionWithAnswersDTO> getQuestionById(@PathVariable int questionId)
			throws NotFoundException {
		QuestionWithAnswersDTO questionDTO = service.getQuestionById(questionId);
		return new ResponseEntity<>(questionDTO, HttpStatus.OK);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Add a new question")
	public ResponseEntity<QuestionInput> addNewQuestion(@RequestBody @Valid QuestionInput questionRequest)
			throws NotFoundException {
		service.createQuestion(questionRequest);
		return new ResponseEntity<>(questionRequest, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update question")
	public ResponseEntity<QuestionInput> updateQuestion(@RequestBody QuestionInput questionRequest,
			@PathVariable int id) throws NotFoundException {
		service.updateQuestion(questionRequest, id);
		return new ResponseEntity<>(questionRequest, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete question")
	public ResponseEntity<MessageResponse> deleteQuestion(@PathVariable int id) {
		service.deleteQuestion(id);
		String message = "Question " + id + " is deleted";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
	}

}

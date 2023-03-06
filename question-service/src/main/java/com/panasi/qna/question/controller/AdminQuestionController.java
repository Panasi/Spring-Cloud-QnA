package com.panasi.qna.question.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.panasi.qna.question.exception.CategoryNotExistException;
import com.panasi.qna.question.payload.MessageResponse;
import com.panasi.qna.question.payload.QuestionRequest;
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
	public ResponseEntity<List<QuestionDTO>> showAllQuestions(@RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getAllQuestions(access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}
	
	@GetMapping("/category/{categoryId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get questions from certain category")
	public ResponseEntity<List<QuestionDTO>> showQuestionsFromCategory(@PathVariable int categoryId, @RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getCategoryQuestions(categoryId, access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}
	
	@GetMapping("/subcategory/{categoryId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get questions from certain category and all its subcategories")
	public ResponseEntity<List<QuestionDTO>> showQuestionsFromSubcategories(@PathVariable int categoryId, @RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> result = new ArrayList<>();
		List<QuestionDTO> allSubQuestionDtos = service.getSubcategoriesQuestions(categoryId, access, result);
		return new ResponseEntity<>(allSubQuestionDtos, HttpStatus.OK);
	}
	
	@GetMapping("/user/{authorId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all user questions")
	public ResponseEntity<List<QuestionDTO>> showAllUserQuestions(@PathVariable int authorId, @RequestParam(defaultValue = "all") String access) {
		List<QuestionDTO> allQuestionDtos = service.getUserQuestions(authorId, access);
		return new ResponseEntity<>(allQuestionDtos, HttpStatus.OK);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Add a new question")
	public ResponseEntity<QuestionRequest> addNewQuestion(@RequestBody QuestionRequest questionRequest) throws CategoryNotExistException {
		service.createQuestion(questionRequest);
		return new ResponseEntity<>(questionRequest, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update question")
	public ResponseEntity<QuestionRequest> updateQuestion(@RequestBody QuestionRequest questionRequest, @PathVariable int id) throws NotFoundException {
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

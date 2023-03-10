package com.panasi.qna.answer.controller;

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

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.exception.ForbiddenException;
import com.panasi.qna.answer.payload.AnswerRequest;
import com.panasi.qna.answer.service.UserAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/answers")
public class UserAnswerController {

	private final UserAnswerService service;

	@GetMapping("/user/{authorId}")
	@Operation(summary = "Get user answers")
	public ResponseEntity<List<AnswerDTO>> getUserAnswers(@PathVariable int authorId,
			@RequestParam(defaultValue = "all") String access) {
		List<AnswerDTO> allAnswerDTOs = service.getUserAnswers(authorId, access);
		return new ResponseEntity<>(allAnswerDTOs, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get answer by id")
	public ResponseEntity<AnswerDTO> getAnswerById(@PathVariable int id) throws NotFoundException, ForbiddenException {
		AnswerDTO answerDTO = service.getAnswerById(id);
		return new ResponseEntity<>(answerDTO, HttpStatus.OK);
	}

	@PostMapping()
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Add a new answer")
	public ResponseEntity<AnswerRequest> addNewAnswer(@RequestBody AnswerRequest answerRequest)
			throws NotFoundException {
		service.createAnswer(answerRequest);
		return new ResponseEntity<>(answerRequest, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('USER')")
	@Operation(summary = "Update answer")
	public ResponseEntity<AnswerRequest> updateAnswer(@RequestBody AnswerRequest answerRequest, @PathVariable int id)
			throws NotFoundException, ForbiddenException {
		service.updateAnswer(answerRequest, id);
		return new ResponseEntity<>(answerRequest, HttpStatus.ACCEPTED);
	}

}

package com.panasi.qna.answer.controller;

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

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.payload.AnswerInput;
import com.panasi.qna.answer.payload.MessageResponse;
import com.panasi.qna.answer.service.AdminAnswerService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/answers")
public class AdminAnswerController {

	private final AdminAnswerService service;

	@GetMapping()
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all answers")
	public ResponseEntity<List<AnswerDTO>> getAllAnswers(@RequestParam(defaultValue = "all") String access) {
		List<AnswerDTO> allAnswerDTOs = service.getAllAnswers(access);
		return new ResponseEntity<>(allAnswerDTOs, HttpStatus.OK);
	}

	@GetMapping("/user/{authorId}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all user answers")
	public ResponseEntity<List<AnswerDTO>> getUserAnswers(@PathVariable int authorId,
			@RequestParam(defaultValue = "all") String access) {
		List<AnswerDTO> allAnswerDTOs = service.getAllUserAnswers(authorId, access);
		return new ResponseEntity<>(allAnswerDTOs, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get answer by id")
	public ResponseEntity<AnswerDTO> getAnswerById(@PathVariable int id) throws NotFoundException {
		AnswerDTO answerDTO = service.getAnswerById(id);
		return new ResponseEntity<>(answerDTO, HttpStatus.OK);
	}

	@PostMapping()
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Add a new answer")
	public ResponseEntity<AnswerInput> addNewAnswer(@RequestBody @Valid AnswerInput answerRequest)
			throws NotFoundException {
		service.createAnswer(answerRequest);
		return new ResponseEntity<>(answerRequest, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update answer")
	public ResponseEntity<AnswerInput> updateAnswer(@RequestBody AnswerInput answerRequest, @PathVariable int id)
			throws NotFoundException {
		service.updateAnswer(answerRequest, id);
		return new ResponseEntity<>(answerRequest, HttpStatus.ACCEPTED);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete answer")
	public ResponseEntity<MessageResponse> deleteAnswer(@PathVariable int id) {
		service.deleteAnswer(id);
		String message = "Answer " + id + " deleted";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
	}

}

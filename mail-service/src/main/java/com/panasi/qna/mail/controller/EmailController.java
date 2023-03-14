package com.panasi.qna.mail.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.mail.model.EmailDetails;
import com.panasi.qna.mail.payload.MessageResponse;
import com.panasi.qna.mail.service.EmailService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/email")
public class EmailController {

	private final EmailService emailService;

	@PostMapping("/sendEmail")
	public ResponseEntity<MessageResponse> sendEmail(@RequestBody EmailDetails details) {
		boolean status = emailService.sendEmail(details);
		if (status) {
			String message = "Mail sent successfully";
			return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
		}
		String message = "Error while sending mail";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.NOT_FOUND);
	}

}

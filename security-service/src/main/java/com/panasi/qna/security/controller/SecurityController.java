package com.panasi.qna.security.controller;

import javax.validation.Valid;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.security.exception.DuplicateException;
import com.panasi.qna.security.payload.JwtResponse;
import com.panasi.qna.security.payload.MessageResponse;
import com.panasi.qna.security.payload.SignInInput;
import com.panasi.qna.security.payload.SignUpInput;
import com.panasi.qna.security.service.AuthService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class SecurityController {

	private final AuthService authService;

	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticateUser(@RequestBody @Valid SignInInput signInRequest)
			throws NotFoundException {

		JwtResponse response = authService.signInUser(signInRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
		
	}

	@PostMapping("/signout")
	public ResponseEntity<MessageResponse> logoutUser(@RequestHeader("Authorization") String auth)
			throws NotFoundException {
		
		authService.signOutUser(auth);

		String message = "You have successfully log out.";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
		
	}

	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody @Valid SignUpInput signUpRequest)
			throws DuplicateException {
		
		authService.signUpUser(signUpRequest);

		String message = "You have successfully registered.";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);

	}

}

package com.panasi.qna.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.security.exception.DuplicateRegistrationException;
import com.panasi.qna.security.payload.JwtResponse;
import com.panasi.qna.security.payload.MessageResponse;
import com.panasi.qna.security.payload.SignInRequest;
import com.panasi.qna.security.payload.SignUpRequest;
import com.panasi.qna.security.service.AuthService;

import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class SecurityController {
	
	private final AuthService authService;
	
	@PostMapping("/signin")
	public ResponseEntity<JwtResponse> authenticateUser(@RequestBody SignInRequest signInRequest) {
		
		JwtResponse response = authService.singnInUser(signInRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/signup")
	public ResponseEntity<MessageResponse> registerUser(@RequestBody SignUpRequest signUpRequest) throws DuplicateRegistrationException {
		authService.signUpUser(signUpRequest);
		
		String message = "You have successfully registered.";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
		
	}

}

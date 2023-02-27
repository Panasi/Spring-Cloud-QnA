package com.panasi.qna.eurekaclient.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

	@GetMapping("/helloworld")
	public ResponseEntity<String> printHelloWorld() {
		String message = "Hello World!";
		return new ResponseEntity<>(message, HttpStatus.OK);
	}
	
}

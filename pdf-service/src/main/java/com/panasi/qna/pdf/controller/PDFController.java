package com.panasi.qna.pdf.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.pdf.model.QuestionWithAnswersDTO;
import com.panasi.qna.pdf.service.PDFService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/pdf")
public class PDFController {

	@PostMapping("/download")
	@Operation(summary = "Create and return PDF file")
	public ResponseEntity<InputStreamResource> createPdf(@RequestHeader String fileName, @RequestHeader String title,
			@RequestBody List<QuestionWithAnswersDTO> questions) throws IOException {
		PDFService.createPDF(fileName, title, questions);
		File filePDF = new File("./src/main/resources/temp/" + fileName);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(filePDF));
		HttpHeaders header = new HttpHeaders();
		header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		header.add("Cache-Control", "no-cache, no-store, must-revalidate");
		header.add("Pragma", "no-cache");
		header.add("Expires", "0");
		return ResponseEntity.ok().headers(header).contentLength(filePDF.length())
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

}

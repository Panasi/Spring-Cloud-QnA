package com.panasi.qna.pdf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PdfServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PdfServiceApplication.class, args);
	}

}

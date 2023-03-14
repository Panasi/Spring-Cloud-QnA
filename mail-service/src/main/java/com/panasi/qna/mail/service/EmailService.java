package com.panasi.qna.mail.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.mail.model.EmailDetails;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender javaMailSender;
	
	private RestTemplate restTemplate = new RestTemplate();

	private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

	public boolean sendEmail(EmailDetails emailDetails) {
		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom(emailDetails.getSender());
			mailMessage.setTo(emailDetails.getRecipient());
			mailMessage.setText(emailDetails.getMsgBody());
			mailMessage.setSubject(emailDetails.getSubject());
			javaMailSender.send(mailMessage);
			return true;
		} catch (Exception e) {
			logger.error("Error while sending mail", e);
			return false;
		}
	}

	public boolean sendAuthEmail(String recipient) {

		try {
			SimpleMailMessage mailMessage = new SimpleMailMessage();
			mailMessage.setFrom("noreply@questionsandanswers.com");
			mailMessage.setTo(recipient);
			mailMessage.setText("You have successfully registered in 'Questions and answers' application.");
			mailMessage.setSubject("Welcome!");
			javaMailSender.send(mailMessage);
			return true;
		} catch (Exception e) {
			logger.error("Error while sending mail", e);
			return false;
		}

	}

	@Scheduled(fixedDelay = 1000 * 60 * 24)
	public void sendReminderEmail() {
		List<String> inactiveUserEmails = getInactiveUserEmails();
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom("noreply@questionsandanswers.com");
		mailMessage.setText("You haven't ask any questions yet. Try it now!");
		mailMessage.setSubject("Reminder!");
		inactiveUserEmails.forEach(email -> {
			mailMessage.setTo(email);
			try {
				javaMailSender.send(mailMessage);
			} catch (Exception e) {
				logger.error("Error while sending mail", e);
			}
		});

	}
	
	private List<String> getInactiveUserEmails() {
		String url = "http://localhost:8765/auth/emails/inactive";
		ResponseEntity<List<String>> response = restTemplate.exchange(url, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<String>>() {
				});
		return response.getBody();
	}

}

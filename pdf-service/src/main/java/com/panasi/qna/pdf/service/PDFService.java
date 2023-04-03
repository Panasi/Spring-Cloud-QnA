package com.panasi.qna.pdf.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.stereotype.Service;

import com.panasi.qna.pdf.mapper.QuestionListMapper;
import com.panasi.qna.pdf.model.QuestionWithAnswersDTO;
import com.panasi.qna.pdf.util.PDFUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PDFService {

	private final MessageConverter messageConverter;
	private final QuestionListMapper questionListMapper;

	@RabbitListener(queues = "getQuestionsPDFQueue")
	public byte[] getPDF(Message message) throws IOException {
		String title = (String) message.getMessageProperties().getHeaders().get("title");
		String fileName = (String) message.getMessageProperties().getHeaders().get("fileName");
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rawQuestionsList = (List<Map<String, Object>>) messageConverter.fromMessage(message);

		List<QuestionWithAnswersDTO> questionsWithAnswers = questionListMapper.toQuestionList(rawQuestionsList);

		return PDFUtil.createPDF(title, fileName, questionsWithAnswers);
	}

}

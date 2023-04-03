package com.panasi.qna.pdf.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.panasi.qna.pdf.model.AnswerDTO;
import com.panasi.qna.pdf.model.QuestionWithAnswersDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionListMapper {

	public List<QuestionWithAnswersDTO> toQuestionList(List<Map<String, Object>> questionsList) {

		List<QuestionWithAnswersDTO> questionsWithAnswers = new ArrayList<>();

		for (Map<String, Object> questionMap : questionsList) {
			QuestionWithAnswersDTO question = new QuestionWithAnswersDTO();
			question.setId((Integer) questionMap.get("id"));
			question.setCategoryId((Integer) questionMap.get("categoryId"));
			question.setIsPrivate((Boolean) questionMap.get("isPrivate"));
			question.setContent((String) questionMap.get("content"));
			question.setAuthorId((Integer) questionMap.get("authorId"));
			String dateString = (String) questionMap.get("date");
			LocalDateTime date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			question.setDate(date);
			question.setRating((Double) questionMap.get("rating"));

			@SuppressWarnings("unchecked")
			List<Map<String, Object>> answersList = (List<Map<String, Object>>) questionMap.get("answers");
			List<AnswerDTO> answers = new ArrayList<>();
			for (Map<String, Object> answerMap : answersList) {
				AnswerDTO answer = new AnswerDTO();
				answer.setId((Integer) answerMap.get("id"));
				answer.setQuestionId((Integer) answerMap.get("questionId"));
				answer.setIsPrivate((Boolean) answerMap.get("isPrivate"));
				answer.setContent((String) answerMap.get("content"));
				answer.setAuthorId((Integer) answerMap.get("authorId"));
				dateString = (String) answerMap.get("date");
				date = LocalDateTime.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
				answer.setDate(date);
				answer.setRating((Double) answerMap.get("rating"));
				answers.add(answer);
			}
			question.setAnswers(answers);

			questionsWithAnswers.add(question);
		}
		return questionsWithAnswers;

	}
}

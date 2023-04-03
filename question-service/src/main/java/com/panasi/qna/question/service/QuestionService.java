package com.panasi.qna.question.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;

import com.panasi.qna.question.dto.AnswerDTO;
import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.entity.Question;
import com.panasi.qna.question.mapper.FullQuestionMapper;
import com.panasi.qna.question.mapper.QuestionMapper;
import com.panasi.qna.question.payload.QuestionRequest;
import com.panasi.qna.question.payload.Utils;
import com.panasi.qna.question.repository.QuestionRepository;

@Service
public class QuestionService {

	@Autowired
	protected QuestionRepository questionRepository;
	@Autowired
	protected QuestionMapper questionMapper;
	@Autowired
	protected FullQuestionMapper fullQuestionMapper;
	@Autowired
	protected RabbitTemplate rabbitTemplate;
	@Autowired
	protected MessageConverter messageConverter;

	protected static final String PUBLIC = "public";
	protected static final String PRIVATE = "private";
	protected static final String ALL = "all";

	// Add a new question
	public void createQuestion(QuestionRequest questionRequest) throws NotFoundException {
		if (!isCategoryExists(questionRequest.getCategoryId())) {
			throw new NotFoundException();
		}

		int currentUserId = Utils.getCurrentUserId();
		LocalDateTime dateTime = LocalDateTime.now();
		QuestionDTO questionDTO = QuestionDTO.builder().content(questionRequest.getContent())
				.categoryId(questionRequest.getCategoryId()).isPrivate(questionRequest.getIsPrivate())
				.authorId(currentUserId).date(dateTime).build();
		Question question = questionMapper.toQuestion(questionDTO);
		questionRepository.save(question);
	}

	// Sort questions
	public List<QuestionDTO> sortQuestionsDTO(List<QuestionDTO> questionsDTO) {
		List<QuestionDTO> sortedQuestionsDTO = new ArrayList<>(questionsDTO);
		sortedQuestionsDTO.sort((q1, q2) -> {
			int compare = q1.getCategoryId().compareTo(q2.getCategoryId());
			if (compare == 0) {
				compare = Boolean.compare(q1.getIsPrivate(), q2.getIsPrivate());
				if (compare == 0) {
					compare = Optional.ofNullable(q1.getRating()).orElse(Double.MIN_VALUE)
							.compareTo(Optional.ofNullable(q2.getRating()).orElse(Double.MIN_VALUE));
					if (compare == 0) {
						compare = q1.getDate().compareTo(q2.getDate());
					}
				}
			}
			return compare;
		});
		return sortedQuestionsDTO;
	}

	// Sort questions with answers
	public List<QuestionWithAnswersDTO> sortQuestionsWithAnswersDTO(List<QuestionWithAnswersDTO> questionsDTO) {
		List<QuestionWithAnswersDTO> sortedQuestionsDTO = new ArrayList<>(questionsDTO);
		sortedQuestionsDTO.sort((q1, q2) -> {
			int compare = q1.getCategoryId().compareTo(q2.getCategoryId());
			if (compare == 0) {
				compare = Boolean.compare(q1.getIsPrivate(), q2.getIsPrivate());
				if (compare == 0) {
					compare = Optional.ofNullable(q1.getRating()).orElse(Double.MIN_VALUE)
							.compareTo(Optional.ofNullable(q2.getRating()).orElse(Double.MIN_VALUE));
					if (compare == 0) {
						compare = q1.getDate().compareTo(q2.getDate());
					}
				}
			}
			return compare;
		});
		return sortedQuestionsDTO;
	}

	// Return list of subcategory id from category service
	public List<Integer> getAllSubcategoryId(int categoryId) {
	    ParameterizedTypeReference<List<Integer>> typeRef = new ParameterizedTypeReference<List<Integer>>() {};
	    return rabbitTemplate.convertSendAndReceiveAsType("getAllSubcategoryIdQueue", categoryId, typeRef);
	}

	// Return is category exists from category service
	public Boolean isCategoryExists(int categoryId) {
		return (Boolean) rabbitTemplate.convertSendAndReceive("isCategoryExistsQueue", categoryId);
	}

	// Return category name from category service
	public String getCategoryName(int categoryId) {
		return (String) rabbitTemplate.convertSendAndReceive("getCategoryNameQueue", categoryId);
	}

	// Return list of question answers from answer service
	public List<AnswerDTO> getAnswersByQuestion(int questionId) {
		ParameterizedTypeReference<List<AnswerDTO>> typeRef = new ParameterizedTypeReference<List<AnswerDTO>>() {};
	    return rabbitTemplate.convertSendAndReceiveAsType("getAnswersByQuestionQueue", questionId, typeRef);
	}

	// Return list of answers by question and author from answer service
	public List<AnswerDTO> getAnswersByQuestionAndAuthor(int questionId, int authorId) {
		ParameterizedTypeReference<List<AnswerDTO>> typeRef = new ParameterizedTypeReference<List<AnswerDTO>>() {};
		List<Integer> params = Arrays.asList(questionId, authorId);
	    return rabbitTemplate.convertSendAndReceiveAsType("getAnswersByQuestionAndAuthorQueue", params, typeRef);
	}

	// Return question rating from comment service
	public Double getRating(int questionId) {
		Double rating = (Double) rabbitTemplate.convertSendAndReceive("getQuestionRatingQueue", questionId);
		return rating != 0.0 ? (Math.ceil(rating * 100) / 100) : null;
	}

	// Return list of question id by category
	@RabbitListener(queues = "getCategoryQuestionsCountQueue")
	public Integer getAllQuestionIdByCategory(int categoryId) {
		return questionRepository.countByCategoryId(categoryId);
	}

	// Return is question exists
	@RabbitListener(queues = "isQuestionExistsQueue")
	public boolean isQuestionExists(int questionId) {
		return questionRepository.existsById(questionId);
	}

	// Return question isPrivate value by question id
	@RabbitListener(queues = "getQuestionIsPrivateQueue")
	public boolean getQuestionIsPrivate(int questionId) throws NotFoundException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);
		return question.getIsPrivate();
	}

	// Return question authorId value by question id
	@RabbitListener(queues = "getQuestionAuthorIdQueue")
	public int getQuestionAuthorId(int questionId) throws NotFoundException {
		Question question = questionRepository.findById(questionId).orElseThrow(NotFoundException::new);
		return question.getAuthorId();
	}

}

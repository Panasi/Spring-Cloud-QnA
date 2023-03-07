package com.panasi.qna.answer.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.entity.Answer;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
	
	AnswerDTO toAnswerDTO(Answer answer);
	List<AnswerDTO> toAnswerDTOs(List<Answer> answers);
	Answer toAnswer(AnswerDTO answerDTO);

}

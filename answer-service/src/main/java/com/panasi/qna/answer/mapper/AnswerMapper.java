package com.panasi.qna.answer.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.panasi.qna.answer.dto.AnswerDTO;
import com.panasi.qna.answer.entity.Answer;

@Mapper(componentModel = "spring")
public interface AnswerMapper {
	
	@Mapping(target = "rating", ignore = true)
	AnswerDTO toAnswerDTO(Answer answer);
	List<AnswerDTO> toAnswerDTOs(List<Answer> answers);
	Answer toAnswer(AnswerDTO answerDTO);

}

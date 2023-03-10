package com.panasi.qna.question.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.entity.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {

	@Mapping(target = "rating", ignore = true)
	QuestionDTO toQuestionDTO(Question question);

	List<QuestionDTO> toQuestionDTOs(List<Question> questions);

	Question toQuestion(QuestionDTO questionDTO);

}

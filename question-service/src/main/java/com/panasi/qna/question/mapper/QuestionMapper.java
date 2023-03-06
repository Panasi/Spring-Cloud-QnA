package com.panasi.qna.question.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panasi.qna.question.dto.QuestionDTO;
import com.panasi.qna.question.entity.Question;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
	
	QuestionDTO toQuestionDto(Question question);
	List<QuestionDTO> toQuestionDTOs(List<Question> questions);
	Question toQuestion(QuestionDTO questionDto);

}
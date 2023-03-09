package com.panasi.qna.question.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.panasi.qna.question.dto.QuestionWithAnswersDTO;
import com.panasi.qna.question.entity.Question;

@Mapper(componentModel = "spring")
public interface FullQuestionMapper {
	
	@Mapping(target = "rating", ignore = true)
	@Mapping(target = "answers", ignore = true)
	QuestionWithAnswersDTO toFullQuestionDTO(Question question);
	List<QuestionWithAnswersDTO> toFullQuestionDTOs(List<Question> questions);

}

package com.panasi.qna.comment.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panasi.qna.comment.dto.QuestionCommentDTO;
import com.panasi.qna.comment.entity.QuestionComment;

@Mapper(componentModel = "spring")
public interface QuestionCommentMapper {

	QuestionCommentDTO toCommentDTO(QuestionComment comment);

	List<QuestionCommentDTO> toCommentDTOs(List<QuestionComment> comments);

	QuestionComment toComment(QuestionCommentDTO commentDTO);

}

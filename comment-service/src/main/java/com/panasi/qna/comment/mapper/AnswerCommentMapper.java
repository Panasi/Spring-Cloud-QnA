package com.panasi.qna.comment.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panasi.qna.comment.dto.AnswerCommentDTO;
import com.panasi.qna.comment.entity.AnswerComment;

@Mapper(componentModel = "spring")
public interface AnswerCommentMapper {

	AnswerCommentDTO toCommentDTO(AnswerComment comment);

	List<AnswerCommentDTO> toCommentDTOs(List<AnswerComment> comments);

	AnswerComment toComment(AnswerCommentDTO commentDTO);

}

package com.panasi.qna.category.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panasi.qna.category.dto.CategoryDTO;
import com.panasi.qna.category.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	
	CategoryDTO toCategoryDTO(Category category);
	List<CategoryDTO> toCategoryDTOs(List<Category> categories);
	Category toCategory(CategoryDTO categoryDto);

}

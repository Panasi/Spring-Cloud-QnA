package com.panasi.qna.category.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.panasi.qna.category.dto.CategoryDto;
import com.panasi.qna.category.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
	
	CategoryDto toCategoryDto(Category category);
	List<CategoryDto> toCategoryDtos(List<Category> categories);
	Category toCategory(CategoryDto categoryDto);

}

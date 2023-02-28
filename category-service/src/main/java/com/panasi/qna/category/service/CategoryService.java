package com.panasi.qna.category.service;

import java.util.List;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.panasi.qna.category.dto.CategoryDto;
import com.panasi.qna.category.entity.Category;
import com.panasi.qna.category.mapper.CategoryMapper;
import com.panasi.qna.category.payload.CategoryRequest;
import com.panasi.qna.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
	
	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	private final RestTemplate restTemplate = new RestTemplate();
	
	// Return list of all categories
	public List<CategoryDto> getAllCategories() {
		return categoryMapper.toCategoryDtos(categoryRepository.findAll());
	}
		
	// Return list of subcategories of certain category
	public List<CategoryDto> getAllSubcategories(int parentId) {
		return categoryMapper.toCategoryDtos(categoryRepository.findAllByParentId(parentId));
	}
		
	// Return category by id
	public CategoryDto getCategoryById(int categoryId) throws NotFoundException {
		Category category = categoryRepository.findById(categoryId)
	            .orElseThrow(NotFoundException::new);
	    return categoryMapper.toCategoryDto(category);
	}
		
	// Add a new category
	public void createCategory(CategoryRequest categoryRequest) {
		CategoryDto categoryDto = CategoryDto.builder()
			.name(categoryRequest.getName())
			.parentId(categoryRequest.getParentId())
			.build();
		Category category = categoryMapper.toCategory(categoryDto);
		categoryRepository.save(category);
	}
		
	// Update certain category
	@Transactional
	public void updateCategory(CategoryRequest categoryRequest, int categoryId) throws NotFoundException {
		Category category = categoryRepository.findById(categoryId)
	            .orElseThrow(NotFoundException::new);
		if (Objects.nonNull(categoryRequest.getName())) {
			category.setName(categoryRequest.getName());
		}
		if (Objects.nonNull(categoryRequest.getParentId())) {
			category.setParentId(categoryRequest.getParentId());
		}
		categoryRepository.save(category);
	}
		
	// Delete certain category
	@Transactional
	public void deleteCategory(int categoryId) throws NotFoundException {
	    Category category = categoryRepository.findById(categoryId)
	            .orElseThrow(NotFoundException::new);

	    List<Category> subcategories = categoryRepository.findAllByParentId(categoryId);
	    for (Category subcategory : subcategories) {
	        deleteCategory(subcategory.getId());
	    }

	    List<Integer> categoryQuestionsId = getQuestionsIdFromCategory();
	    categoryQuestionsId.forEach(id -> restTemplate.delete("http://question-service/questions/{id}", id));

	    categoryRepository.delete(category);
	}
	
	// Return list of question id from category
	public List<Integer> getQuestionsIdFromCategory() {
	    String url = "http://localhost:8765/questions/id/category/{id}";
	    ResponseEntity<List<Integer>> response = restTemplate.exchange(
	      url,
	      HttpMethod.GET,
	      null,
	      new ParameterizedTypeReference<List<Integer>>() {}
	    );
	    return response.getBody();
	}

}

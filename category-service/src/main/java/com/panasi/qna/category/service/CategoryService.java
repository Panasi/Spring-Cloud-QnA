package com.panasi.qna.category.service;

import java.util.List;
import java.util.Objects;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.panasi.qna.category.dto.CategoryDTO;
import com.panasi.qna.category.entity.Category;
import com.panasi.qna.category.exception.CategoryIsNotEmptyException;
import com.panasi.qna.category.mapper.CategoryMapper;
import com.panasi.qna.category.payload.CategoryRequest;
import com.panasi.qna.category.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;
	private final CategoryMapper categoryMapper;
	private final RabbitTemplate rabbitTemplate;

	// Return list of all categories
	public List<CategoryDTO> getAllCategories() {
		return categoryMapper.toCategoryDTOs(categoryRepository.findAll());
	}

	// Return list of subcategories of certain category
	public List<CategoryDTO> getAllSubcategories(int parentId) {
		return categoryMapper.toCategoryDTOs(categoryRepository.findAllByParentId(parentId));
	}

	// Return category by id
	public CategoryDTO getCategoryById(int categoryId) throws NotFoundException {
		Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);
		return categoryMapper.toCategoryDTO(category);
	}

	// Add a new category
	public void createCategory(CategoryRequest categoryRequest) {
		CategoryDTO categoryDTO = CategoryDTO.builder().name(categoryRequest.getName())
				.parentId(categoryRequest.getParentId()).build();
		Category category = categoryMapper.toCategory(categoryDTO);
		categoryRepository.save(category);
	}

	// Update certain category
	@Transactional
	public void updateCategory(CategoryRequest categoryRequest, int categoryId) throws NotFoundException {
		Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);
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
	public void deleteCategory(int categoryId) throws NotFoundException, CategoryIsNotEmptyException {
		Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);

		List<Category> subcategories = categoryRepository.findAllByParentId(categoryId);
		for (Category subcategory : subcategories) {
			deleteCategory(subcategory.getId());
		}

		Integer categoryQuestionsCount = getCategoryQuestionsCount(categoryId);
		if (categoryQuestionsCount > 0) {
			throw new CategoryIsNotEmptyException("Category or subcategory is not empty");
		}
		categoryRepository.delete(category);
	}

	// Return category questions count from question service
	public Integer getCategoryQuestionsCount(int categoryId) {
		return (Integer) rabbitTemplate.convertSendAndReceive("getCategoryQuestionsCountQueue", categoryId);
	}

	// Return list of subcategory id
	@RabbitListener(queues = "getAllSubcategoryIdQueue")
	public List<Integer> getAllSubcategoryId(int parentId) {
		return categoryRepository.findAllCategoryIdByParentId(parentId);
	}

	// Return is category exists
	@RabbitListener(queues = "isCategoryExistsQueue")
	public Boolean isCategoryExists(int categoryId) {
		return categoryRepository.existsById(categoryId);
	}
	
	// Return categories name
	@RabbitListener(queues = "getCategoryNameQueue")
	public String getCategoryName(int categoryId) throws NotFoundException {
		Category category = categoryRepository.findById(categoryId).orElseThrow(NotFoundException::new);
		return category.getName();
	}

}

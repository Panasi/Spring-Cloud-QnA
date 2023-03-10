package com.panasi.qna.category.controller;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.category.dto.CategoryDTO;
import com.panasi.qna.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/categories")
public class UserCategoryController {

	private final CategoryService categoryService;

	@GetMapping
	@Operation(summary = "Get all categories")
	public ResponseEntity<List<CategoryDTO>> getAllCategories() {
		List<CategoryDTO> allCategoryDtos = categoryService.getAllCategories();
		return new ResponseEntity<>(allCategoryDtos, HttpStatus.OK);
	}

	@GetMapping("/{id}/subcategories")
	@Operation(summary = "Get all subcategories")
	public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable int id) {
		List<CategoryDTO> allCategoryDtos = categoryService.getAllSubcategories(id);
		return new ResponseEntity<>(allCategoryDtos, HttpStatus.OK);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a category by id")
	public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable int id) throws NotFoundException {
		CategoryDTO categoryDto = categoryService.getCategoryById(id);
		return new ResponseEntity<>(categoryDto, HttpStatus.OK);
	}

}

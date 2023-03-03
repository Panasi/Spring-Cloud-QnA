package com.panasi.qna.category.controller;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.category.dto.CategoryDto;
import com.panasi.qna.category.payload.CategoryRequest;
import com.panasi.qna.category.payload.MessageResponse;
import com.panasi.qna.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/categories")
public class AdminController {
	
	private final CategoryService categoryService;
	
	@GetMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all categories")
	public ResponseEntity<List<CategoryDto>> showAllCategories() {
		List<CategoryDto> allCategoryDtos = categoryService.getAllCategories();
		return new ResponseEntity<>(allCategoryDtos, HttpStatus.OK);
	}
	
	@GetMapping("/{id}/subcategories")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get all subcategories")
	public ResponseEntity<List<CategoryDto>> showSubcategories(@PathVariable int id) {
		List<CategoryDto> allCategoryDtos = categoryService.getAllSubcategories(id);
		return new ResponseEntity<>(allCategoryDtos, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Get a category by id")
	public ResponseEntity<CategoryDto> showCategoryById(@PathVariable int id) throws NotFoundException {
		CategoryDto categoryDto = categoryService.getCategoryById(id);
		return new ResponseEntity<>(categoryDto, HttpStatus.OK);
	}
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Add a new category")
	public ResponseEntity<CategoryRequest> addNewCategory(@RequestBody CategoryRequest categoryRequest) {
		categoryService.createCategory(categoryRequest);
		return new ResponseEntity<>(categoryRequest, HttpStatus.CREATED);
	}
	
	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Update category")
	public ResponseEntity<CategoryRequest> updateCategory(@RequestBody CategoryRequest categoryRequest, @PathVariable int id) throws NotFoundException {
		categoryService.updateCategory(categoryRequest, id);
		return new ResponseEntity<>(categoryRequest, HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@Operation(summary = "Delete category and all subcategories")
	public ResponseEntity<MessageResponse> deleteCategory(@PathVariable int id) throws NotFoundException {
		categoryService.deleteCategory(id);
		String message = "Category " + id + " and all its subcategories are deleted";
		return new ResponseEntity<>(new MessageResponse(message), HttpStatus.OK);
	}

}

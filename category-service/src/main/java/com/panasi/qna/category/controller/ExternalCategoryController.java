package com.panasi.qna.category.controller;

import java.util.List;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.panasi.qna.category.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping("/external/categories")
public class ExternalCategoryController {

	private final CategoryService categoryService;

	@GetMapping("/subcategoriesId/{id}")
	@Operation(summary = "Get all subcategory id")
	public List<Integer> getSubcategoryIds(@PathVariable int id) {
		return categoryService.getAllSubcategoryId(id);
	}

	@GetMapping("/exists/{id}")
	@Operation(summary = "Is category exists")
	public boolean isCategoryExists(@PathVariable int id) {
		return categoryService.isCategoryExists(id);
	}
	
	@GetMapping("/{id}/name")
	@Operation(summary = "Is category exists")
	public String getCategoryName(@PathVariable int id) throws NotFoundException {
		return categoryService.getCategoryName(id);
	}

}

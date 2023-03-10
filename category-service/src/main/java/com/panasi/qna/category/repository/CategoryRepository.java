package com.panasi.qna.category.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.panasi.qna.category.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

	public Category findByName(String name);

	public List<Category> findAllByParentId(int parentId);

	@Query("SELECT c.id FROM Category c WHERE c.parentId = ?1")
	public List<Integer> findAllCategoryIdByParentId(int parentId);

}

package com.ecommerce.project.service;

import java.util.List;

import com.ecommerce.project.model.Category;

public interface CategoryService {

	List<Category> getAllCategories();

	void createCategory(Category category);

	String deleteCatgory(Long categoryId);

	Category updateCategory(Category category, Long categoryId);

}

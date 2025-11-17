package com.ecommerce.project.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.project.model.Category;

@Service
public class CategoryServiceImpl implements CategoryService {
	private List<Category> categories = new ArrayList<Category>();
	private Long indexId = 1L;

	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		return categories;
	}

	@Override
	public void createCategory(Category category) {
		// TODO Auto-generated method stub
		category.setCategoryId(indexId++);
		categories.add(category);

	}

	@Override
	public String deleteCatgory(Long categoryId) {
		// TODO Auto-generated method stub
		Category category = categories.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found"));
		categories.remove(category);
		return "Category with categoryId "+categoryId+" is removed Successfully";
	}

	@Override
	public Category updateCategory(Category category, Long categoryId) {
		// TODO Auto-generated method stub
		Category updatedCategory = categories.stream().filter(c -> c.getCategoryId().equals(categoryId)).findFirst()
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category Not Found"));
		updatedCategory.setCategoryName(category.getCategoryName());
		return updatedCategory;
	}

}

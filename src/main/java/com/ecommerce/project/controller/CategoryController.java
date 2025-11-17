package com.ecommerce.project.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;

@RestController
@RequestMapping("/api")
public class CategoryController {
	@Autowired
	private CategoryService categoryService;

	@GetMapping("/public/categories")
	public ResponseEntity<List<Category>> getAllCateegory() {
		List<Category> categories = categoryService.getAllCategories();
		return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
	}
	@PostMapping("/public/categories")
	public ResponseEntity<String> createCategory(@RequestBody Category category){
		categoryService.createCategory(category);
		return new ResponseEntity<String>("Category added Successfully",HttpStatus.CREATED);
	}
	
	@DeleteMapping("admin/categories/{categoryId}")
	public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId){
		try {
			String status=categoryService.deleteCatgory(categoryId);
			return new ResponseEntity<String>(status,HttpStatus.OK);
		}
		catch(ResponseStatusException e) {
			return new ResponseEntity<String>(e.getReason(),e.getStatusCode());
		}
	}
	
	@PutMapping("public/categories/{categoryId}")
	public ResponseEntity<String> updateCategory(@RequestBody Category category,@PathVariable Long categoryId){
		try {
			Category savedCategory= categoryService.updateCategory(category,categoryId);
			return new ResponseEntity<String>("Category with category id "+categoryId,HttpStatus.OK);
		}catch(ResponseStatusException e) {
			return new ResponseEntity<String>(e.getReason(),e.getStatusCode());
		}
	}
}
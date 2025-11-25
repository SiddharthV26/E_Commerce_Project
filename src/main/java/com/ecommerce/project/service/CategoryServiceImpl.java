package com.ecommerce.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
//	private List<Category> categories = new ArrayList<Category>();
//	private Long indexId = 1L;
	@Autowired
	private CategoryRepository categoryRepositoy;

	@Override
	public List<Category> getAllCategories() {
		// TODO Auto-generated method stub
		List<Category> categories=categoryRepositoy.findAll();
		if(categories.isEmpty()) {
			throw new APIException("No category till now.");
		}
		return categories;
	}

	@Override
	public void createCategory(Category category) {
		// TODO Auto-generated method stub
		Category savedCategory=categoryRepositoy.findByCategoryName(category.getCategoryName());
		if(savedCategory!=null) {
			throw new APIException("Category with the name "+category.getCategoryName()+" already exists !!!");
		}
		categoryRepositoy.save(category);
	}

	@Override
	public String deleteCatgory(Long categoryId) {
		// TODO Auto-generated method stub
		Optional<Category> optional = categoryRepositoy.findById(categoryId);
		if(optional.isEmpty()) {
			throw new APIException("No category with "+categoryId+" is present in the list");
		}
		Category category=optional.get();
		categoryRepositoy.delete(category);
		return "Category with categoryId " + categoryId + " is removed Successfully";
	}

	@Override
	public Category updateCategory(Category category, Long categoryId) {
		// TODO Auto-generated method stub
		Optional<Category> optional = categoryRepositoy.findById(categoryId);
		if(optional.isEmpty()) {
			throw new APIException("No category with "+categoryId+" is present in the list");
		}
		Category updatedCategory=optional.get();
		updatedCategory.setCategoryId(categoryId);
		updatedCategory.setCategoryName(category.getCategoryName());
		categoryRepositoy.save(updatedCategory);
		return updatedCategory;
	}

}

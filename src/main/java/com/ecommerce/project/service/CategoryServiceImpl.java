package com.ecommerce.project.service;

import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
//	private List<Category> categories = new ArrayList<Category>();
//	private Long indexId = 1L;
	@Autowired
	private CategoryRepository categoryRepositoy;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		// TODO Auto-generated method stub
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Category> categoryPage = categoryRepositoy.findAll(pageDetails);

		List<Category> categories = categoryPage.getContent();
		if (categories.isEmpty())
			throw new APIException("No category created till now.");

		List<CategoryDTO> categoryDTOS = categories.stream()
				.map(category -> modelMapper.map(category, CategoryDTO.class)).toList();

		CategoryResponse categoryResponse = new CategoryResponse();
		categoryResponse.setContent(categoryDTOS);
		categoryResponse.setPageNumber(categoryPage.getNumber());
		categoryResponse.setPageSize(categoryPage.getSize());
		categoryResponse.setTotalElements(categoryPage.getTotalElements());
		categoryResponse.setTotalPages(categoryPage.getTotalPages());
		categoryResponse.setLastPage(categoryPage.isLast());

		return categoryResponse;
	}

	@Override
	public CategoryDTO createCategory(CategoryDTO categoryDTO) {
		// TODO Auto-generated method stub
		Category category=modelMapper.map(categoryDTO, Category.class);
		Category categoryFromDb = categoryRepositoy.findByCategoryName(category.getCategoryName());
		if (categoryFromDb != null) {
			throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!");
		}
		Category savedCategory=categoryRepositoy.save(category);
		return modelMapper.map(savedCategory, CategoryDTO.class);
	}

	@Override
	public CategoryDTO deleteCatgory(Long categoryId) {
		// TODO Auto-generated method stub
		Optional<Category> optional = categoryRepositoy.findById(categoryId);
		if (optional.isEmpty()) {
			throw new APIException("No category with " + categoryId + " is present in the list");
		}
		Category category = optional.get();
		categoryRepositoy.delete(category);
		return  modelMapper.map(category,CategoryDTO.class);
	}

	@Override
	public CategoryDTO updateCategory(CategoryDTO categoryDTO, Long categoryId) {
		// TODO Auto-generated method stub
		
		Optional<Category> optional = categoryRepositoy.findById(categoryId);
		if (optional.isEmpty()) {
			throw new APIException("No category with " + categoryId + " is present in the list");
		}
		Category category=modelMapper.map(categoryDTO, Category.class);
		Category updatedCategory = optional.get();
		updatedCategory.setCategoryId(categoryId);
		updatedCategory.setCategoryName(category.getCategoryName());
		categoryRepositoy.save(updatedCategory);
		return modelMapper.map(updatedCategory, CategoryDTO.class);
	}

}

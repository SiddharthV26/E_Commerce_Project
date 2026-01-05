package com.ecommerce.project.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.project.exception.APIException;
import com.ecommerce.project.exception.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repository.CategoryRepository;
import com.ecommerce.project.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ModelMapper modelMapper;
	@Autowired
	private FileService fileService;
	@Value("${project.images}")
	private String path;

	@Override
	public ProductDTO addProduct(Long categoryId, ProductDTO productDto) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryID", categoryId));

		boolean isProductNotPresent = true;

		List<Product> products = category.getProducts();
		for (Product value : products) {
			if (value.getProductName().equals(productDto.getProductName())) {
				isProductNotPresent = false;
				break;
			}
		}
		if (isProductNotPresent) {
			Product product = modelMapper.map(productDto, Product.class);
			product.setImage("default.png");
			product.setCategory(category);
			double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
			product.setSpecialPrice(specialPrice);
			Product savedProduct = productRepository.save(product);
			return modelMapper.map(savedProduct, ProductDTO.class);
		} else {
			throw new APIException("Product already exists!!");
		}
	}

	@Override
	public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
		// TODO Auto-generated method stub
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productPage = productRepository.findAll(pageDetails);
		List<Product> products = productPage.getContent();
		List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.toList();
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOS);
		productResponse.setPageNumber(productPage.getNumber());
		productResponse.setPageSize(productPage.getSize());
		productResponse.setTotalElements(productPage.getTotalElements());
		productResponse.setTotalPages(productPage.getTotalPages());
		productResponse.setLastPage(productPage.isLast());

		return productResponse;
	}

	@Override
	public ProductResponse searchByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {
		// TODO Auto-generated method stub
		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResourceNotFoundException("Category", "categoryID", categoryId));
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productPage = productRepository.findByCategoryOrderByPriceAsc(category, pageDetails);
		List<Product> products = productPage.getContent();

		List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.toList();
		if (products.isEmpty()) {
			throw new APIException("Product not found with keyword " + categoryId);
		}
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOS);
		productResponse.setPageNumber(productPage.getNumber());
		productResponse.setPageSize(productPage.getSize());
		productResponse.setTotalElements(productPage.getTotalElements());
		productResponse.setTotalPages(productPage.getTotalPages());
		productResponse.setLastPage(productPage.isLast());

		return productResponse;

	}

	@Override
	public ProductResponse searchProductByKeyword(String keyword, Integer pageNumber, Integer pageSize, String sortBy,
			String sortOrder) {
		// TODO Auto-generated method stub
		Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending()
				: Sort.by(sortBy).descending();
		Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
		Page<Product> productPage = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%', pageDetails);
		List<Product> products = productPage.getContent();

		List<ProductDTO> productDTOS = products.stream().map(product -> modelMapper.map(product, ProductDTO.class))
				.toList();
		if (products.isEmpty()) {
			throw new APIException("Product not found with keyword " + keyword);
		}
		ProductResponse productResponse = new ProductResponse();
		productResponse.setContent(productDTOS);
		productResponse.setPageNumber(productPage.getNumber());
		productResponse.setPageSize(productPage.getSize());
		productResponse.setTotalElements(productPage.getTotalElements());
		productResponse.setTotalPages(productPage.getTotalPages());
		productResponse.setLastPage(productPage.isLast());
		return productResponse;

	}

	@Override
	public ProductDTO updateProduct(ProductDTO productDto, Long productId) {
		// TODO Auto-generated method stub
		Product product = modelMapper.map(productDto, Product.class);
		Product productFromDb = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		productFromDb.setProductName(product.getProductName());
		productFromDb.setDescription(product.getDescription());
		productFromDb.setQuantity(product.getQuantity());
		productFromDb.setDiscount(product.getDiscount());
		productFromDb.setPrice(product.getPrice());
		double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
		productFromDb.setSpecialPrice(specialPrice);

		Product savedProduct = productRepository.save(productFromDb);
		return modelMapper.map(savedProduct, ProductDTO.class);
	}

	@Override
	public ProductDTO deleteProduct(Long productId) {
		// TODO Auto-generated method stub
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
		productRepository.delete(product);
		return modelMapper.map(product, ProductDTO.class);

	}

	@Override
	public ProductDTO updateProductImage(Long productId, MultipartFile image) throws IOException {
		// TODO Auto-generated method stub
		Product productFromDb = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

		String fileName = fileService.uploadImage(path, image);
		productFromDb.setImage(fileName);
		Product updatedProduct = productRepository.save(productFromDb);

		return modelMapper.map(updatedProduct, ProductDTO.class);

	}

}

package com.devsuperior.dscatalog.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dscatalog.dto.ProductDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dscatalog.tests.Factory;

@ExtendWith(SpringExtension.class) // unit test
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	private long existingId;
	private long nonExistingId;
	private Long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private ProductDTO dto;
	private Category category;

	@BeforeEach
	void setUp() {
		existingId = 1L;
		nonExistingId = 1000L;
		dependentId = 4L;
		product = Factory.createProduct();
		category = Factory.createCategory();
		dto = Factory.createProductDTO();
		page = new PageImpl<>(List.of(product));

		when(categoryRepository.getOne(existingId)).thenReturn(category);
		when(categoryRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		when(productRepository.getOne(existingId)).thenReturn(product);
		when(productRepository.getOne(nonExistingId)).thenThrow(EntityNotFoundException.class);

		when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);
		when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
		when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
		when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);
		
		

		doNothing().when(productRepository).deleteById(existingId);
		doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
		doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);
	}

	@Test
	public void findAllPagedShouldReturnPage() {
		Pageable pageable = PageRequest.of(0, 10);

		Page<ProductDTO> result = service.findAllPaged(pageable);

		Assertions.assertNotNull(result);
		verify(productRepository, Mockito.times(1)).findAll(pageable);
	}

	@Test
	public void findbyIdShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.findById(existingId);

		Assertions.assertNotNull(result);
		verify(productRepository, Mockito.times(1)).findById(existingId);
	}

	@Test
	public void findbyIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.findById(nonExistingId);
		});
		verify(productRepository, Mockito.times(1)).findById(nonExistingId);
	}

	@Test
	public void updateShouldReturnProductDTOWhenIdExists() {
		ProductDTO result = service.update(existingId, dto);

		Assertions.assertNotNull(result);
		verify(productRepository, Mockito.times(1)).getOne(existingId);
		verify(productRepository, Mockito.times(1)).save(product);
	}

	@Test
	public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.update(nonExistingId, dto);
		});
		verify(productRepository, Mockito.times(1)).getOne(nonExistingId);
	}

	@Test
	public void deleteShouldDoNothingWhenIdExists() {

		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});

		verify(productRepository, Mockito.times(1)).deleteById(existingId);
	}

	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistingId);
		});

		verify(productRepository, Mockito.times(1)).deleteById(nonExistingId);
	}

	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {

		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});

		verify(productRepository, Mockito.times(1)).deleteById(dependentId);
	}
}

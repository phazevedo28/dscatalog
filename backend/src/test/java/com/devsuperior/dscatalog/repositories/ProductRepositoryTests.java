package com.devsuperior.dscatalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import com.devsuperior.dscatalog.entities.Product;
import com.devsuperior.dscatalog.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;

	private long countTotalProducts;

	@BeforeEach
	void setUp() {
		countTotalProducts = 25L;
	}

	@Test
	public void saveShouldPersistWithAutoIncrementWhenIdIsNull() {
		// Arrange
		Product product = Factory.creatProductWithIdNull();

		// Act
		product = repository.save(product);

		// Assert
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());

	}

	@Test
	public void deleteByIdShouldDeleteObjectWhenIdExists() {
		// Arrange
		long existingId = 1L;

		// Act
		repository.deleteById(existingId);

		// Assert
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertFalse(result.isPresent());
	}

	@Test
	public void deleteByIdShouldThrowEmptyResultDataAcssesExceptionWhenIdDoesNotExist() {

		Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
			repository.deleteById(26L);
		});
	}
	
	@Test
	public void findByIdShouldReturnNullWhenIdNotExists() {
		
		// Act
		Optional<Product> result = repository.findById(countTotalProducts + 1);
		
		//Assert
		Assertions.assertTrue(result.isEmpty());
	}
	
	@Test
	public void findByIdShouldReturnNotNullWhenIdExists() {
		
		// Act
		Optional<Product> result = repository.findById(countTotalProducts);
		
		//Assert
		Assertions.assertTrue(result.isPresent());
	}
}

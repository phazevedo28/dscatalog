package com.devsuperior.dscatalog.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

public class ProductDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private String name;
	private String description;
	private Double price;
	private String imgUrl;

	private List<CategoryDTO> categories = new ArrayList<>();

	public ProductDTO() {
	}

	public ProductDTO(Long id, String name, String description, Double price, String imgUrl) {
		this.id = id;
		this.setName(name);
		this.setDescription(description);
		this.setPrice(price);
		this.setImgUrl(imgUrl);
	}

	public ProductDTO(Product entity) {
		this.id = entity.getId();
		this.setName(entity.getName());
		this.setDescription(entity.getDescription());
		this.setPrice(entity.getPrice());
		this.setImgUrl(entity.getImgUrl());
	}

	public ProductDTO(Product entity, Set<Category> categories) {
		this(entity);
		categories.forEach(categorie -> this.categories.add(new CategoryDTO(categorie)));
	}
	
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public List<CategoryDTO> getCategories() {
		return categories;
	}

	public void setCategories(List<CategoryDTO> categories) {
		this.categories = categories;
	}

}

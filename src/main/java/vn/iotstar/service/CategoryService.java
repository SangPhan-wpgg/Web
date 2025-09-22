package vn.iotstar.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.Category;

public interface CategoryService {

	List<Category> findAll();

	Page<Category> findAll(Pageable pageable);

	Category findById(int id);

	Category save(Category category);

	void deleteById(int id);

	List<Category> findByCategoryNameContaining(String name);

	Page<Category> findByCategoryNameContaining(String name, Pageable pageable);

	Category findByCategoryName(String name);

	// Tìm kiếm fuzzy với nhiều điều kiện
	Page<Category> searchCategoriesFuzzy(String search, Pageable pageable);
}

package vn.iotstar.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.Category;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public List<Category> findAll() {
		return categoryRepository.findAll();
	}

	@Override
	public Page<Category> findAll(Pageable pageable) {
		return categoryRepository.findAll(pageable);
	}

	@Override
	public Category findById(int id) {
		return categoryRepository.findById(id).orElse(null);
	}

	@Override
	public Category save(Category category) {
		return categoryRepository.save(category);
	}

	@Override
	public void deleteById(int id) {
		categoryRepository.deleteById(id);
	}

	@Override
	public List<Category> findByCategoryNameContaining(String name) {
		return categoryRepository.findByCategoryNameContaining(name);
	}

	@Override
	public Page<Category> findByCategoryNameContaining(String name, Pageable pageable) {
		return categoryRepository.findByCategoryNameContaining(name, pageable);
	}

	@Override
	public Category findByCategoryName(String name) {
		return categoryRepository.findByCategoryName(name);
	}

	@Override
	public Page<Category> searchCategoriesFuzzy(String search, Pageable pageable) {
		return categoryRepository.searchCategoriesFuzzy(search, pageable);
	}
}

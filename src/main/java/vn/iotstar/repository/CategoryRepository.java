package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstar.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	// Tìm kiếm theo tên (LIKE %name%)
	List<Category> findByCategoryNameContaining(String name);

	// Tìm kiếm theo tên + phân trang
	Page<Category> findByCategoryNameContaining(String name, Pageable pageable);

	// Tìm kiếm fuzzy với nhiều điều kiện
	@Query("SELECT c FROM Category c WHERE " + "(:search IS NULL OR "
			+ " LOWER(c.categoryName) LIKE LOWER(CONCAT('%', :search, '%')) OR "
			+ " LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%')))")
	Page<Category> searchCategoriesFuzzy(@Param("search") String search, Pageable pageable);

	// Tìm kiếm theo tên chính xác
	Category findByCategoryName(String name);
}

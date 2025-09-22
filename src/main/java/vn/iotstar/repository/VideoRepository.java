package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Video;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {

	// Tìm video theo category
	List<Video> findByCategoryId(int categoryId);

	// Tìm video theo user
	List<Video> findByUserId(int userId);

	// Tìm video theo trạng thái active
	List<Video> findByActiveTrue();

	// Tìm video theo category và trạng thái active
	List<Video> findByCategoryIdAndActiveTrue(int categoryId);

	// Tìm kiếm video theo title
	@Query("SELECT v FROM Video v WHERE v.title LIKE %:title% AND v.active = true")
	List<Video> findByTitleContainingAndActiveTrue(@Param("title") String title);

	// Tìm kiếm video theo title (tất cả, bao gồm inactive)
	@Query("SELECT v FROM Video v WHERE LOWER(v.title) LIKE LOWER(CONCAT('%', :title, '%'))")
	List<Video> findByTitleContainingIgnoreCase(@Param("title") String title);

	// Tìm video phổ biến (nhiều view nhất)
	@Query("SELECT v FROM Video v WHERE v.active = true ORDER BY v.views DESC")
	List<Video> findTopVideosByViews();

	// Tìm video mới nhất
	@Query("SELECT v FROM Video v WHERE v.active = true ORDER BY v.createdDate DESC")
	List<Video> findLatestVideos();

	// Đếm số video theo category
	@Query("SELECT COUNT(v) FROM Video v WHERE v.category.id = :categoryId AND v.active = true")
	long countByCategoryIdAndActiveTrue(@Param("categoryId") int categoryId);

	// Tìm kiếm video với nhiều điều kiện và phân trang (fuzzy search)
	@Query("SELECT v FROM Video v WHERE " + 
			"(:search IS NULL OR " +
			" LOWER(v.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
			" LOWER(v.description) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
			" LOWER(v.videoCode) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
			"(:categoryId IS NULL OR v.category.id = :categoryId) AND " +
			"(:status IS NULL OR " +
			"  (:status = 'active' AND v.active = true) OR " +
			"  (:status = 'inactive' AND v.active = false))")
	Page<Video> searchVideos(@Param("search") String search, @Param("categoryId") Integer categoryId,
			@Param("status") String status, Pageable pageable);
}

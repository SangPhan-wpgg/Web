package vn.iotstar.service;

import vn.iotstar.entity.Video;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VideoService {
    
    // Lưu video
    Video save(Video video);
    
    // Tìm video theo ID
    Optional<Video> findById(int id);
    
    // Lấy tất cả video
    List<Video> findAll();
    
    // Lấy tất cả video với phân trang
    Page<Video> findAll(Pageable pageable);
    
    // Lấy tất cả video active
    List<Video> findAllActive();
    
    // Tìm video theo category
    List<Video> findByCategoryId(int categoryId);
    
    // Tìm video theo user
    List<Video> findByUserId(int userId);
    
    // Tìm kiếm video theo title
    List<Video> searchByTitle(String title);
    
    // Lấy video phổ biến
    List<Video> findTopVideosByViews(int limit);
    
    // Lấy video mới nhất
    List<Video> findLatestVideos(int limit);
    
    // Xóa video theo ID
    void deleteById(int id);
    
    // Cập nhật số lượt xem
    void incrementViews(int videoId);
    
    // Đếm số video theo category
    long countByCategoryId(int categoryId);
    
    // Kiểm tra video có tồn tại không
    boolean existsById(int id);
    
    // Tìm kiếm video với nhiều điều kiện và phân trang
    Page<Video> searchVideos(String search, Integer categoryId, String status, Pageable pageable);
}

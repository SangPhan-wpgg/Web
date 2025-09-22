package vn.iotstar.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Video;
import vn.iotstar.repository.VideoRepository;
import vn.iotstar.service.VideoService;

import java.util.List;
import java.util.Optional;

@Service 
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideoRepository videoRepository;

    @Override
    public Video save(Video video) {
        return videoRepository.save(video);
    }

    @Override
    public Optional<Video> findById(int id) {
        return videoRepository.findById(id);
    }

    @Override
    public List<Video> findAll() {
        return videoRepository.findAll();
    }
    
    @Override
    public Page<Video> findAll(Pageable pageable) {
        return videoRepository.findAll(pageable);
    }

    @Override
    public List<Video> findAllActive() {
        return videoRepository.findByActiveTrue();
    }

    @Override
    public List<Video> findByCategoryId(int categoryId) {
        return videoRepository.findByCategoryIdAndActiveTrue(categoryId);
    }

    @Override
    public List<Video> findByUserId(int userId) {
        return videoRepository.findByUserId(userId);
    }

    @Override
    public List<Video> searchByTitle(String title) {
        // Tìm kiếm video theo title (chỉ video active)
        return videoRepository.findByTitleContainingAndActiveTrue(title);
    }

    @Override
    public List<Video> findTopVideosByViews(int limit) {
        List<Video> allVideos = videoRepository.findTopVideosByViews();
        return allVideos.stream().limit(limit).toList();
    }

    @Override
    public List<Video> findLatestVideos(int limit) {
        List<Video> allVideos = videoRepository.findLatestVideos();
        return allVideos.stream().limit(limit).toList();
    }

    @Override
    public void deleteById(int id) {
        videoRepository.deleteById(id);
    }

    @Override
    public void incrementViews(int videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            video.setViews(video.getViews() + 1);
            videoRepository.save(video);
        }
    }

    @Override
    public long countByCategoryId(int categoryId) {
        return videoRepository.countByCategoryIdAndActiveTrue(categoryId);
    }

    @Override
    public boolean existsById(int id) {
        return videoRepository.existsById(id);
    }
    
    @Override
    public Page<Video> searchVideos(String search, Integer categoryId, String status, Pageable pageable) {
        return videoRepository.searchVideos(search, categoryId, status, pageable);
    }
}

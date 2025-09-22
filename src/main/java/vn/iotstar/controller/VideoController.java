package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.VideoService;
import vn.iotstar.util.FileUploadUtil;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/videos")
@PreAuthorize("hasRole('ADMIN')")
public class VideoController {

	@Autowired
	private VideoService videoService;

	@Autowired
	private CategoryService categoryService;

	// Hiển thị danh sách video với phân trang
	@GetMapping({ "", "/", "/list" })
	public String list(Model model,
	                   @RequestParam(defaultValue = "0") int page,
	                   @RequestParam(defaultValue = "10") int size,
	                   @RequestParam(defaultValue = "id") String sortBy,
	                   @RequestParam(defaultValue = "desc") String sortDir,
	                   @RequestParam(required = false) String search,
	                   @RequestParam(required = false) Integer categoryId,
	                   @RequestParam(required = false) String status,
	                   HttpServletRequest request) {
		
		System.out.println("=== VideoController.list() called ===");
		System.out.println("Page: " + page + ", Size: " + size + ", Sort: " + sortBy + " " + sortDir);
		System.out.println("Search: " + search + ", Category: " + categoryId + ", Status: " + status);
		
		// Tạo Sort object
		Sort sort = sortDir.equalsIgnoreCase("desc") ? 
			Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		// Tạo Pageable object
		Pageable pageable = PageRequest.of(page, size, sort);
		
		// Tìm kiếm hoặc lấy tất cả
		Page<Video> videoPage;
		if (StringUtils.hasText(search) || categoryId != null || status != null) {
			System.out.println("Using searchVideos with: search=" + search + ", categoryId=" + categoryId + ", status=" + status);
			videoPage = videoService.searchVideos(search, categoryId, status, pageable);
		} else {
			System.out.println("Using findAll - no filters");
			videoPage = videoService.findAll(pageable);
		}
		
		// Đảm bảo search không null hoặc empty
		if (search == null || search.trim().isEmpty()) {
			search = null;
		}
		
		System.out.println("Found " + videoPage.getTotalElements() + " videos in " + videoPage.getTotalPages() + " pages");
		
		model.addAttribute("title", "Videos - Admin");
		model.addAttribute("pageTitle", "Videos");
		model.addAttribute("currentPath", request.getRequestURI());
		model.addAttribute("videos", videoPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", videoPage.getTotalPages());
		model.addAttribute("totalItems", videoPage.getTotalElements());
		model.addAttribute("pageSize", size);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("search", search);
		model.addAttribute("categoryId", categoryId);
		model.addAttribute("status", status);
		model.addAttribute("searchTitle", search); // Đảm bảo searchTitle không null
		
		// Thêm danh sách categories cho filter
		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);
		
		// Thêm tên category được chọn cho hiển thị
		if (categoryId != null) {
			Category selectedCategory = categoryService.findById(categoryId);
			if (selectedCategory != null) {
				model.addAttribute("selectedCategoryName", selectedCategory.getCategoryName());
			}
		}
		
		System.out.println("Returning view: admin/videos/list");
		return "admin/videos/list";
	}

	// Hiển thị form thêm video
	@GetMapping("/add")
	public String addForm(Model model) {
		Video video = new Video();
		List<Category> categories = categoryService.findAll();
		model.addAttribute("video", video);
		model.addAttribute("categories", categories);
		return "admin/videos/addOrEdit";
	}

	// Hiển thị form sửa video
	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Video> videoOpt = videoService.findById(id);
		if (videoOpt.isPresent()) {
			List<Category> categories = categoryService.findAll();
			model.addAttribute("video", videoOpt.get());
			model.addAttribute("categories", categories);
			return "admin/videos/addOrEdit";
		} else {
			redirectAttributes.addFlashAttribute("error", "Video không tồn tại!");
			return "redirect:/admin/videos/";
		}
	}

	// Lưu video (thêm mới hoặc cập nhật)
	@PostMapping("/saveOrUpdate")
	public String saveOrUpdate(@RequestParam(value = "id", required = false) Integer id,
			@RequestParam("title") String title,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "videoCode", required = false) String videoCode,
			@RequestParam(value = "category", required = false) Integer categoryId,
			@RequestParam(value = "duration", required = false) Integer duration,
			@RequestParam(value = "active", required = false) Boolean active,
			@RequestParam(value = "videoFile", required = false) MultipartFile videoFile,
			@RequestParam(value = "posterFile", required = false) MultipartFile posterFile, HttpSession session,
			RedirectAttributes redirectAttributes) {
		
		
		// Validation
		if (title == null || title.trim().isEmpty()) {
			redirectAttributes.addFlashAttribute("error", "Tiêu đề video không được để trống!");
			return "redirect:/admin/videos/add";
		}
		
		
		try {
			// Tạo hoặc lấy video object
			Video video;
			if (id != null && id > 0) {
				// Cập nhật video hiện có
				Optional<Video> videoOpt = videoService.findById(id);
				if (videoOpt.isPresent()) {
					video = videoOpt.get();
				} else {
					redirectAttributes.addFlashAttribute("error", "Video không tồn tại!");
					return "redirect:/admin/videos/";
				}
			} else {
				// Tạo video mới
				video = new Video();
				// Set user cho video mới
				User currentUser = (User) session.getAttribute("user");
				if (currentUser != null) {
					video.setUser(currentUser);
				}
			}

			// Set các thuộc tính cơ bản
			video.setTitle(title);
			video.setDescription(description);
			// Tạo unique video code nếu không có
			if (videoCode != null && !videoCode.trim().isEmpty()) {
				video.setVideoCode(videoCode.trim());
			} else {
				// Tạo unique video code
				String uniqueCode = "VID_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
				video.setVideoCode(uniqueCode);
			}
			video.setDuration(duration != null ? duration : 0);
			video.setActive(active != null ? active : true);

			// Set category nếu có
			if (categoryId != null && categoryId > 0) {
				try {
					Category category = categoryService.findById(categoryId);
					if (category != null) {
						video.setCategory(category);
					}
				} catch (Exception e) {
					// Category không tồn tại, bỏ qua
				}
			}

			// Xử lý upload video file
			if (videoFile != null && !videoFile.isEmpty()) {
				if (FileUploadUtil.isVideoFile(videoFile.getOriginalFilename())) {
					String videoPath = FileUploadUtil.saveFile(videoFile, "videos");
					video.setVideoFile(videoPath);
				} else {
					redirectAttributes.addFlashAttribute("error", "File video không hợp lệ!");
					return "redirect:/admin/videos/add";
				}
			}

			// Xử lý upload poster file
			if (posterFile != null && !posterFile.isEmpty()) {
				if (FileUploadUtil.isImageFile(posterFile.getOriginalFilename())) {
					String posterPath = FileUploadUtil.saveFile(posterFile, "images");
					video.setPoster(posterPath);
				} else {
					redirectAttributes.addFlashAttribute("error", "File poster không hợp lệ!");
					return "redirect:/admin/videos/add";
				}
			}

			videoService.save(video);
			redirectAttributes.addFlashAttribute("success", "Video đã được lưu thành công!");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi khi upload file: " + e.getMessage());
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Lỗi khi lưu video: " + e.getMessage());
		}
		return "redirect:/admin/videos/";
	}

	// Xem chi tiết video
	@GetMapping("/detail/{id}")
	public String detail(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
		Optional<Video> videoOpt = videoService.findById(id);
		if (videoOpt.isPresent()) {
			model.addAttribute("video", videoOpt.get());
			return "admin/videos/detail";
		} else {
			redirectAttributes.addFlashAttribute("error", "Video không tồn tại!");
			return "redirect:/admin/videos/";
		}
	}

	// Xóa video
	@PostMapping("/delete/{id}")
	public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		System.out.println("=== VideoController.delete() called ===");
		System.out.println("Deleting video with ID: " + id);
		
		try {
			Optional<Video> videoOpt = videoService.findById(id);
			if (!videoOpt.isPresent()) {
				System.out.println("Video not found with ID: " + id);
				redirectAttributes.addFlashAttribute("error", "Video not found!");
				return "redirect:/admin/videos/";
			}
			
			Video video = videoOpt.get();
			System.out.println("Found video to delete: " + video.getTitle());
			
			// Xóa file video và poster nếu có
			if (video.getVideoFile() != null && !video.getVideoFile().isEmpty()) {
				System.out.println("Deleting video file: " + video.getVideoFile());
				FileUploadUtil.deleteFile(video.getVideoFile());
			}
			if (video.getPoster() != null && !video.getPoster().isEmpty()) {
				System.out.println("Deleting poster file: " + video.getPoster());
				FileUploadUtil.deleteFile(video.getPoster());
			}
			
			videoService.deleteById(id);
			System.out.println("Successfully deleted video with ID: " + id);
			redirectAttributes.addFlashAttribute("success", "Video '" + video.getTitle() + "' đã được xóa thành công!");
		} catch (Exception e) {
			System.err.println("Error deleting video with ID " + id + ": " + e.getMessage());
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa video: " + e.getMessage());
		}
		return "redirect:/admin/videos/";
	}

	// Tìm kiếm video
	@GetMapping("/search")
	public String search(@RequestParam("title") String title, Model model) {
		List<Video> videos = videoService.searchByTitle(title);
		model.addAttribute("videos", videos);
		model.addAttribute("searchTitle", title);
		return "admin/videos/list";
	}

	// Phát video
	@GetMapping("/play/{id}")
	public String play(@PathVariable("id") int id, Model model) {
		Optional<Video> videoOpt = videoService.findById(id);
		if (videoOpt.isPresent()) {
			Video video = videoOpt.get();

			// Tăng lượt xem
			videoService.incrementViews(id);
			model.addAttribute("video", video);
			return "admin/videos/play";
		}
		return "redirect:/admin/videos/";
	}
 
	// Test endpoint để kiểm tra video file
	@GetMapping("/test-video/{filename:.+}")
	public ResponseEntity<String> testVideo(@PathVariable String filename) {
		try {
			String uploadDir = "uploads/";
			java.io.File file = new java.io.File(uploadDir + filename);

			if (file.exists()) {
				return ResponseEntity
						.ok("File exists: " + file.getAbsolutePath() + " (Size: " + file.length() + " bytes)");
			} else {
				return ResponseEntity.notFound().build();
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body("Error: " + e.getMessage());
		}
	}

}

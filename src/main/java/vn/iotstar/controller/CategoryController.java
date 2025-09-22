package vn.iotstar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.iotstar.entity.Category;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.VideoService;
import vn.iotstar.util.FileUploadUtil;

@Controller
@RequestMapping("admin/categories")
@PreAuthorize("hasRole('ADMIN')")
public class CategoryController {

	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private VideoService videoService;

	// Hiển thị form thêm Category - chỉ ADMIN
    @GetMapping("add")
    public String add(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Add Category - Admin");
        model.addAttribute("pageTitle", "Add Category");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("category", new Category());
        return "admin/categories/addOrEdit";
    }

	// Liệt kê toàn bộ Category với phân trang - chỉ ADMIN
	@RequestMapping({ "", "/" })
	public String list(Model model, 
	                   @RequestParam(defaultValue = "0") int page,
	                   @RequestParam(defaultValue = "5") int size,
	                   @RequestParam(defaultValue = "id") String sortBy,
	                   @RequestParam(defaultValue = "asc") String sortDir,
	                   @RequestParam(required = false) String search,
	                   HttpServletRequest request) {
		
		System.out.println("=== CategoryController.list() called ===");
		System.out.println("Page: " + page + ", Size: " + size + ", Sort: " + sortBy + " " + sortDir);
		System.out.println("Search: " + search);
		
		// Tạo Sort object
		Sort sort = sortDir.equalsIgnoreCase("desc") ? 
			Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		// Tạo Pageable object
		Pageable pageable = PageRequest.of(page, size, sort);
		
		// Tìm kiếm fuzzy hoặc lấy tất cả
		Page<Category> categoryPage;
		if (StringUtils.hasText(search)) {
			categoryPage = categoryService.searchCategoriesFuzzy(search, pageable);
		} else {
			categoryPage = categoryService.findAll(pageable);
		}
		
		System.out.println("Found " + categoryPage.getTotalElements() + " categories in " + categoryPage.getTotalPages() + " pages");
		
		model.addAttribute("title", "Categories - Admin");
		model.addAttribute("pageTitle", "Categories");
		model.addAttribute("currentPath", request.getRequestURI());
		model.addAttribute("categories", categoryPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", categoryPage.getTotalPages());
		model.addAttribute("totalItems", categoryPage.getTotalElements());
		model.addAttribute("pageSize", size);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("search", search);
		
		System.out.println("Returning view: admin/categories/list");
		return "admin/categories/list";
	}

	// Lưu hoặc cập nhật Category - chỉ ADMIN
	@PostMapping("saveOrUpdate")
	public String saveOrUpdate(@ModelAttribute Category category, 
	                          @RequestParam("avatarFile") MultipartFile avatarFile,
	                          RedirectAttributes redirectAttributes) {
		try {
			// Xử lý upload avatar
			if (!avatarFile.isEmpty()) {
				if (FileUploadUtil.isImageFile(avatarFile.getOriginalFilename())) {
					String avatarPath = FileUploadUtil.saveFile(avatarFile, "avatars");
					category.setImages(avatarPath);
				} else {
					redirectAttributes.addFlashAttribute("error", "File avatar không hợp lệ!");
					return "redirect:/admin/categories/add";
				}
			}
			
			categoryService.save(category);
			redirectAttributes.addFlashAttribute("success", "Category saved successfully!");
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Error saving category: " + e.getMessage());
		}
		return "redirect:/admin/categories/";
	}

	// Xóa Category theo ID - chỉ ADMIN
	@PostMapping("delete/{id}")
	public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
		System.out.println("=== CategoryController.delete() called ===");
		System.out.println("Deleting category with ID: " + id);
		
		try {
			Category category = categoryService.findById(id);
			if (category == null) {
				System.out.println("Category not found with ID: " + id);
				redirectAttributes.addFlashAttribute("error", "Category not found!");
				return "redirect:/admin/categories/";
			}
			
			System.out.println("Found category to delete: " + category.getCategoryName());
			
			// Kiểm tra xem có videos nào đang tham chiếu đến category này không
			long videoCount = videoService.countByCategoryId(id);
			if (videoCount > 0) {
				System.out.println("Cannot delete category. Found " + videoCount + " videos referencing this category.");
				redirectAttributes.addFlashAttribute("error", 
					"Không thể xóa danh mục '" + category.getCategoryName() + 
					"' vì còn " + videoCount + " video(s) đang sử dụng danh mục này. " +
					"Vui lòng xóa hoặc chuyển các video sang danh mục khác trước khi xóa danh mục.");
				return "redirect:/admin/categories/";
			}
			
			categoryService.deleteById(id);
			System.out.println("Successfully deleted category with ID: " + id);
			redirectAttributes.addFlashAttribute("success", "Category '" + category.getCategoryName() + "' deleted successfully!");
		} catch (Exception e) {
			System.err.println("Error deleting category with ID " + id + ": " + e.getMessage());
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Error deleting category: " + e.getMessage());
		}
		return "redirect:/admin/categories/";
	}

	// Tìm kiếm Category theo tên với phân trang - chỉ ADMIN
	@RequestMapping("search")
	public String search(Model model, 
	                     @RequestParam(defaultValue = "0") int page,
	                     @RequestParam(defaultValue = "5") int size,
	                     @RequestParam(defaultValue = "id") String sortBy,
	                     @RequestParam(defaultValue = "asc") String sortDir,
	                     @RequestParam(name = "name", required = false) String name, 
	                     HttpServletRequest request) {

		System.out.println("=== CategoryController.search() called ===");
		System.out.println("Search name: " + name);
		
		// Tạo Sort object
		Sort sort = sortDir.equalsIgnoreCase("desc") ? 
			Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
		
		// Tạo Pageable object
		Pageable pageable = PageRequest.of(page, size, sort);
		
		// Tìm kiếm fuzzy hoặc lấy tất cả
		Page<Category> categoryPage;
		if (StringUtils.hasText(name)) {
			categoryPage = categoryService.searchCategoriesFuzzy(name, pageable);
		} else {
			categoryPage = categoryService.findAll(pageable);
		}

		model.addAttribute("title", "Search Categories - Admin");
		model.addAttribute("pageTitle", "Search Categories");
		model.addAttribute("currentPath", request.getRequestURI());
		model.addAttribute("categories", categoryPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", categoryPage.getTotalPages());
		model.addAttribute("totalItems", categoryPage.getTotalElements());
		model.addAttribute("pageSize", size);
		model.addAttribute("sortBy", sortBy);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("search", name);
		model.addAttribute("searchName", name);
		return "admin/categories/list";
	}

	// Chỉnh sửa Category - chỉ ADMIN
	@GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        Category category = categoryService.findById(id);
        model.addAttribute("title", "Edit Category - Admin");
        model.addAttribute("pageTitle", "Edit Category");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("category", category);
        return "admin/categories/addOrEdit";
    }

	// Xem chi tiết Category - chỉ ADMIN
	@GetMapping("/detail/{id}")
    public String detail(@PathVariable("id") int id, Model model, HttpServletRequest request) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return "redirect:/admin/categories/";
        }
        
        model.addAttribute("title", "Category Detail - Admin");
        model.addAttribute("pageTitle", "Category Detail");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("category", category);
        
        // Mock data for statistics - replace with actual data later
        model.addAttribute("totalVideos", 15);
        model.addAttribute("totalViews", 12500);
        model.addAttribute("totalLikes", 450);
        model.addAttribute("totalDuration", "2h 30m");
        
        return "admin/categories/detail";
    }
}

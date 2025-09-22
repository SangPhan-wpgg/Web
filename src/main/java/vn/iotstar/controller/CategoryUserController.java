package vn.iotstar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Video;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.VideoService;

@Controller
@RequestMapping("categories")
public class CategoryUserController {

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private VideoService videoService;

	// Xem danh sách danh mục - tất cả user đều có thể xem
	@GetMapping("")
	public String listCategories(Model model) {
		List<Category> categories = categoryService.findAll();
		model.addAttribute("categories", categories);
		return "web/categories/list";
	}

	// Xem chi tiết danh mục - tất cả user đều có thể xem
	@GetMapping("/{id}")
	public String viewCategory(@PathVariable("id") int id, Model model) {
		Category category = categoryService.findById(id);
		if (category != null) {
			// Lấy danh sách video thuộc category này
			List<Video> videos = videoService.findByCategoryId(id);

			model.addAttribute("category", category);
			model.addAttribute("videos", videos);
			return "web/categories/view";
		}
		return "redirect:/categories";
	}
}

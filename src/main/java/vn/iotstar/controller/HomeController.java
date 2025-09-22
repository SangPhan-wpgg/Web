package vn.iotstar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import vn.iotstar.entity.Video;
import vn.iotstar.service.VideoService;

@Controller
@RequestMapping("/")
public class HomeController {

	@Autowired
	private VideoService videoService;

	@GetMapping("")
	public String home(Model model) {
		return "web/index";
	}

	@GetMapping("search")
	public String search(@RequestParam(value = "title", required = false) String title, Model model) {
		if (title != null && !title.trim().isEmpty()) {
			List<Video> videos = videoService.searchByTitle(title.trim());
			model.addAttribute("videos", videos);
			model.addAttribute("searchTitle", title.trim());
		} else {
			model.addAttribute("videos", List.of());
			model.addAttribute("searchTitle", "");
		}
		return "web/search";
	}
}
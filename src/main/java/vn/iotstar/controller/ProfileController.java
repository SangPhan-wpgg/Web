package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;

@Controller
@RequestMapping("profile")
public class ProfileController {

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Hiển thị trang profile
	@GetMapping("")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String profile(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			// Lấy thông tin user mới nhất từ database
			User currentUser = userService.findByUserName(user.getUserName());
			model.addAttribute("user", currentUser);
			session.setAttribute("user", currentUser); // Cập nhật session
		}
		return "web/profile";
	}

	// Cập nhật thông tin profile
	@PostMapping("update")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String updateProfile(@RequestParam("fullName") String fullName, @RequestParam("email") String email,
			@RequestParam("phone") String phone, HttpSession session, RedirectAttributes redirectAttributes) {
		try {
			User sessionUser = (User) session.getAttribute("user");
			if (sessionUser != null) {
				User user = userService.findByUserName(sessionUser.getUserName());
				if (user != null) {
					user.setFullName(fullName);
					user.setEmail(email);
					user.setPhone(phone);

					userService.save(user);

					// Cập nhật session
					session.setAttribute("user", user);

					redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
				}
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin!");
		}

		return "redirect:/profile";
	}

	// Đổi mật khẩu
	@PostMapping("change-password")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String changePassword(@RequestParam("currentPassword") String currentPassword,
			@RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
			HttpSession session, RedirectAttributes redirectAttributes) {
		try {
			User sessionUser = (User) session.getAttribute("user");
			if (sessionUser != null) {
				User user = userService.findByUserName(sessionUser.getUserName());
				if (user != null) {
					// Kiểm tra mật khẩu hiện tại
					if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
						redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
						return "redirect:/profile";
					}

					// Kiểm tra mật khẩu mới
					if (!newPassword.equals(confirmPassword)) {
						redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
						return "redirect:/profile";
					}

					if (newPassword.length() < 8) {
						redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 8 ký tự!");
						return "redirect:/profile";
					}

					// Cập nhật mật khẩu
					user.setPassword(passwordEncoder.encode(newPassword));
					userService.save(user);

					redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
				}
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu!");
		}

		return "redirect:/profile";
	}

	// Hiển thị video của user
	@GetMapping("/my-videos")
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public String myVideos(Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user != null) {
			// Lấy thông tin user mới nhất từ database
			User currentUser = userService.findByUserName(user.getUserName());
			model.addAttribute("user", currentUser);
			model.addAttribute("title", "Video của tôi");
		}
		return "web/my-videos";
	}
}

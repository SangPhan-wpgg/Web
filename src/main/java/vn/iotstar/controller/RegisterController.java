package vn.iotstar.controller;

import vn.iotstar.entity.User;
import vn.iotstar.entity.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import vn.iotstar.service.UserService;
import vn.iotstar.service.RoleService;

import java.util.regex.Pattern;

@Controller
@RequestMapping("register")
public class RegisterController {
	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// Regex patterns for validation
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
	private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10,11}$");
	private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

	@GetMapping("")
	public String getRegister(Model model) {
		model.addAttribute("user", new User());
		return "web/register";
	}

	@PostMapping("")
	public String postRegister(@ModelAttribute User user, @RequestParam("confirmPassword") String confirmPassword,
			RedirectAttributes redirectAttributes) {

		System.out.println("=== POST /register - Bắt đầu xử lý đăng ký ===");

		// Validate input data
		String validationError = validateUserInput(user, confirmPassword);
		if (validationError != null) {
			System.out.println("Validation error: " + validationError);
			redirectAttributes.addFlashAttribute("error", validationError);
			return "redirect:/register";
		}

		// Check for duplicates
		String duplicateError = checkDuplicates(user);
		if (duplicateError != null) {
			System.out.println("Duplicate error: " + duplicateError);
			redirectAttributes.addFlashAttribute("error", duplicateError);
			return "redirect:/register";
		}

		try {
			System.out.println("Bắt đầu lưu user...");
			// Prepare user for saving
			prepareUserForSave(user);

			// Save user
			User savedUser = userService.save(user);
			System.out.println("Đăng ký thành công với ID: " + savedUser.getId());

			redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Bạn có thể đăng nhập ngay.");
			return "redirect:/login";

		} catch (org.springframework.dao.DataIntegrityViolationException e) {
			System.out.println("Lỗi duplicate key: " + e.getMessage());
			// Kiểm tra loại lỗi duplicate
			if (e.getMessage().contains("user_name")) {
				redirectAttributes.addFlashAttribute("error", "Tên đăng nhập này đã được sử dụng!");
			} else if (e.getMessage().contains("email")) {
				redirectAttributes.addFlashAttribute("error", "Email này đã được sử dụng!");
			} else if (e.getMessage().contains("phone")) {
				redirectAttributes.addFlashAttribute("error", "Số điện thoại này đã được sử dụng!");
			} else {
				redirectAttributes.addFlashAttribute("error", "Dữ liệu đã tồn tại trong hệ thống!");
			}
			return "redirect:/register";
		} catch (Exception e) {
			System.out.println("Lỗi khi đăng ký: " + e.getMessage());
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đăng ký. Vui lòng thử lại!");
			return "redirect:/register";
		}
	}

	/**
	 * Validate input data nhanh chóng
	 */
	private String validateUserInput(User user, String confirmPassword) {
		// Kiểm tra các trường bắt buộc
		if (!StringUtils.hasText(user.getFullName())) {
			return "Họ và tên không được để trống!";
		}

		if (!StringUtils.hasText(user.getUserName())) {
			return "Tên đăng nhập không được để trống!";
		}

		if (!USERNAME_PATTERN.matcher(user.getUserName()).matches()) {
			return "Tên đăng nhập chỉ được chứa chữ cái, số và dấu gạch dưới (3-20 ký tự)!";
		}

		if (!StringUtils.hasText(user.getEmail())) {
			return "Email không được để trống!";
		}

		if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
			return "Email không hợp lệ!";
		}

		if (!StringUtils.hasText(user.getPhone())) {
			return "Số điện thoại không được để trống!";
		}

		if (!PHONE_PATTERN.matcher(user.getPhone()).matches()) {
			return "Số điện thoại phải có 10-11 chữ số!";
		}

		if (!StringUtils.hasText(user.getPassword())) {
			return "Mật khẩu không được để trống!";
		}

		if (user.getPassword().length() < 8) {
			return "Mật khẩu phải có ít nhất 8 ký tự!";
		}

		if (!user.getPassword().equals(confirmPassword)) {
			return "Mật khẩu xác nhận không khớp!";
		}

		return null; // Không có lỗi
	}

	/**
	 * Kiểm tra trùng lặp dữ liệu
	 */
	private String checkDuplicates(User user) {
		// Kiểm tra email trùng lặp
		if (userService.existsByEmail(user.getEmail())) {
			return "Email này đã được sử dụng!";
		}

		// Kiểm tra username trùng lặp
		if (userService.existsByUserName(user.getUserName())) {
			return "Tên đăng nhập này đã được sử dụng!";
		}

		// Kiểm tra phone trùng lặp
		if (userService.existsByPhone(user.getPhone())) {
			return "Số điện thoại này đã được sử dụng!";
		}

		return null; // Không có trùng lặp
	}

	/**
	 * Chuẩn bị user để lưu vào database
	 */
	private void prepareUserForSave(User user) {
		// Mã hóa mật khẩu
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// Lấy role USER từ database thay vì tạo mới
		Role userRole = roleService.findByName("USER");
		if (userRole == null) {
			// Nếu không tìm thấy role USER, tạo mới
			userRole = new Role();
			userRole.setRoleName("USER");
			userRole = roleService.save(userRole);
		}
		user.setRole(userRole);

		// Set created date
		user.setCreatedDate(new java.util.Date());
	}
}

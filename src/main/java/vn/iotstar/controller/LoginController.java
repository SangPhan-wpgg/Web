package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("login")
public class LoginController {

	@GetMapping("")
	public String login(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout,
			@RequestParam(value = "userName", required = false) String userName, Model model) {

		if (error != null) {
			model.addAttribute("error", "Tên đăng nhập hoặc mật khẩu không đúng!");
		}

		if (logout != null) {
			model.addAttribute("success", "Bạn đã đăng xuất thành công!");
		}

		if (userName != null) {
			model.addAttribute("userName", userName);
		}

		return "web/login";
	}

}

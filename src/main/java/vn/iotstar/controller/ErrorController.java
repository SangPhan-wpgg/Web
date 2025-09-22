package vn.iotstar.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

	@RequestMapping("/error")
	public String handleError(HttpServletRequest request, Model model) {
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

		if (status != null) {
			Integer statusCode = Integer.valueOf(status.toString());

			if (statusCode == HttpStatus.FORBIDDEN.value()) {
				// 403 Forbidden - User không có quyền truy cập
				Authentication auth = SecurityContextHolder.getContext().getAuthentication();
				if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
					// User đã đăng nhập nhưng không có quyền
					model.addAttribute("errorTitle", "Không có quyền truy cập");
					model.addAttribute("errorMessage",
							"Bạn không có quyền truy cập vào trang này. Vui lòng liên hệ quản trị viên.");
					model.addAttribute("errorCode", "403");
					model.addAttribute("suggestedAction", "Quay về trang chủ");
					model.addAttribute("suggestedUrl", "/");
				} else {
					// User chưa đăng nhập
					return "redirect:/login";
				}
			} else if (statusCode == HttpStatus.NOT_FOUND.value()) {
				// 404 Not Found
				model.addAttribute("errorTitle", "Trang không tìm thấy");
				model.addAttribute("errorMessage", "Trang bạn đang tìm kiếm không tồn tại hoặc đã bị xóa.");
				model.addAttribute("errorCode", "404");
				model.addAttribute("suggestedAction", "Quay về trang chủ");
				model.addAttribute("suggestedUrl", "/");
			} else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
				// 500 Internal Server Error
				model.addAttribute("errorTitle", "Lỗi máy chủ");
				model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xử lý yêu cầu của bạn.");
				model.addAttribute("errorCode", "500");
				model.addAttribute("suggestedAction", "Thử lại sau");
				model.addAttribute("suggestedUrl", "/");
			} else {
				// Các lỗi khác
				model.addAttribute("errorTitle", "Đã xảy ra lỗi");
				model.addAttribute("errorMessage", "Đã xảy ra lỗi không xác định.");
				model.addAttribute("errorCode", statusCode.toString());
				model.addAttribute("suggestedAction", "Quay về trang chủ");
				model.addAttribute("suggestedUrl", "/");
			}
		}

		return "web/error";
	}
}

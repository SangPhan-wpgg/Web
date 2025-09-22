package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		System.out.println("=== Authentication Success Handler ===");
		System.out.println("Authentication: " + authentication);
		System.out.println("Authentication name: " + authentication.getName());
		System.out.println("Authentication authorities: " + authentication.getAuthorities());

		// Lưu user vào session để GlobalModelAttribute có thể sử dụng
		HttpSession session = request.getSession();
		User user = userService.findByUserName(authentication.getName());
		if (user != null) {
			session.setAttribute("user", user);
			System.out.println(
					"User saved to session: " + user.getFullName() + " (Role: " + user.getRole().getRoleName() + ")");
		}

		System.out.println("=== End Authentication Success Handler ===");

		// Redirect dựa trên role
		if (user != null && user.getRole() != null) {
			String roleName = user.getRole().getRoleName();
			if ("ADMIN".equals(roleName)) {
				response.sendRedirect("/admin");
			} else {
				response.sendRedirect("/");
			}
		} else {
			response.sendRedirect("/");
		}
	}
}

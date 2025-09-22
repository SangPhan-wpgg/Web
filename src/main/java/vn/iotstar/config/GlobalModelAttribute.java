package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;

@Component
@ControllerAdvice
public class GlobalModelAttribute {
    
    @Autowired
    private UserService userService;
    
    @ModelAttribute("user")
    public User getCurrentUser(HttpSession session, HttpServletRequest request) {
        // Chỉ xử lý cho các request không phải static resources
        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/css/") || requestURI.startsWith("/js/") || 
            requestURI.startsWith("/images/") || requestURI.startsWith("/video-files/") ||
            requestURI.startsWith("/avatars/") || requestURI.startsWith("/uploads/")) {
            return null;
        }
        
        // Ưu tiên lấy user từ session trước
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return user;
        }
        
        // Nếu không có trong session, thử lấy từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getName().equals("anonymousUser")) {
            
            // Lấy thông tin user từ database
            user = userService.findByUserName(authentication.getName());
            if (user != null) {
                // Lưu vào session để lần sau không cần query lại
                session.setAttribute("user", user);
            }
            return user;
        }
        
        return null;
    }
    
    @ModelAttribute("currentPath")
    public String getCurrentPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}

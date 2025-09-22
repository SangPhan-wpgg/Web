package vn.iotstar.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.iotstar.entity.User;
import vn.iotstar.service.UserService;
import vn.iotstar.util.FileUploadUtil;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("")
    public String dashboard(Model model, HttpServletRequest request) {
        // Lấy thông tin user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userService.findByUserName(currentUserName);
        
        if (currentUser == null) {
            // Nếu không tìm thấy user, tạo user mặc định
            currentUser = new User();
            currentUser.setId(1);
            currentUser.setUserName("admin");
            currentUser.setFullName("Administrator");
            currentUser.setEmail("admin@example.com");
            currentUser.setPhone("0123456789");
            currentUser.setCreatedDate(new java.util.Date());
            currentUser.setAvatar("https://ui-avatars.com/api/?name=Administrator&background=667eea&color=fff&size=128");
            
            // Tạo role mặc định
            vn.iotstar.entity.Role adminRole = new vn.iotstar.entity.Role();
            adminRole.setId(1);
            adminRole.setRoleName("ADMIN");
            currentUser.setRole(adminRole);
        }
        
        // Debug logging
        System.out.println("=== Admin Dashboard Debug ===");
        System.out.println("User: " + currentUser.getUserName());
        System.out.println("Full Name: " + currentUser.getFullName());
        System.out.println("Avatar: " + currentUser.getAvatar());
        System.out.println("=============================");
        
        model.addAttribute("title", "Admin Dashboard");
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("user", currentUser);
        
        // Add dashboard statistics
        model.addAttribute("totalUsers", 150);
        model.addAttribute("totalCategories", 25);
        model.addAttribute("totalVideos", 1200);
        model.addAttribute("totalViews", 50000);
        
        return "admin/dashboard";
    }
    
    @GetMapping("/")
    public String dashboardRedirect() {
        return "redirect:/admin";
    }
    
    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        // Lấy thông tin user hiện tại từ SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserName = authentication.getName();
        User currentUser = userService.findByUserName(currentUserName);
        
        if (currentUser == null) {
            // Nếu không tìm thấy user, tạo user mặc định
            currentUser = new User();
            currentUser.setId(1);
            currentUser.setUserName("admin");
            currentUser.setFullName("Administrator");
            currentUser.setEmail("admin@example.com");
            currentUser.setPhone("0123456789");
            currentUser.setCreatedDate(new java.util.Date());
            currentUser.setAvatar("https://ui-avatars.com/api/?name=Administrator&background=667eea&color=fff&size=128");
            
            // Tạo role mặc định
            vn.iotstar.entity.Role adminRole = new vn.iotstar.entity.Role();
            adminRole.setId(1);
            adminRole.setRoleName("ADMIN");
            currentUser.setRole(adminRole);
        }
        
        // Debug logging
        System.out.println("=== Admin Profile Debug ===");
        System.out.println("User: " + currentUser.getUserName());
        System.out.println("Full Name: " + currentUser.getFullName());
        System.out.println("Avatar: " + currentUser.getAvatar());
        System.out.println("Avatar null? " + (currentUser.getAvatar() == null));
        System.out.println("Avatar empty? " + (currentUser.getAvatar() != null && currentUser.getAvatar().isEmpty()));
        System.out.println("==========================");
        
        model.addAttribute("title", "Admin Profile");
        model.addAttribute("pageTitle", "Profile");
        model.addAttribute("currentPath", request.getRequestURI());
        model.addAttribute("user", currentUser);
        return "admin/profile";
    }
    
    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("fullName") String fullName,
                               @RequestParam("userName") String userName,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam(value = "avatar", required = false) MultipartFile avatarFile,
                               RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserName = authentication.getName();
            User user = userService.findByUserName(currentUserName);
            
            if (user != null) {
                // Cập nhật thông tin
                user.setFullName(fullName);
                user.setUserName(userName);
                user.setEmail(email);
                user.setPhone(phone);
                
                // Xử lý upload avatar nếu có
                if (avatarFile != null && !avatarFile.isEmpty()) {
                    String fileName = FileUploadUtil.saveFile(avatarFile, "avatars");
                    if (fileName != null) {
                        user.setAvatar("/avatars/" + fileName);
                    }
                }
                
                userService.save(user);
                redirectAttributes.addFlashAttribute("success", "Cập nhật thông tin thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
        }
        
        return "redirect:/admin/profile";
    }
    
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                @RequestParam("newPassword") String newPassword,
                                @RequestParam("confirmPassword") String confirmPassword,
                                RedirectAttributes redirectAttributes) {
        try {
            // Lấy thông tin user hiện tại
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            User user = userService.findByUserName(userName);
            
            if (user != null) {
                // Kiểm tra mật khẩu hiện tại
                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "Mật khẩu hiện tại không đúng!");
                    return "redirect:/admin/profile";
                }
                
                // Kiểm tra mật khẩu mới
                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Mật khẩu xác nhận không khớp!");
                    return "redirect:/admin/profile";
                }
                
                if (newPassword.length() < 8) {
                    redirectAttributes.addFlashAttribute("error", "Mật khẩu mới phải có ít nhất 8 ký tự!");
                    return "redirect:/admin/profile";
                }
                
                // Cập nhật mật khẩu
                user.setPassword(passwordEncoder.encode(newPassword));
                userService.save(user);
                
                redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy thông tin người dùng!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra khi đổi mật khẩu: " + e.getMessage());
        }
        
        return "redirect:/admin/profile";
    }
    
    @GetMapping("/settings")
    public String settings(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Admin Settings");
        model.addAttribute("pageTitle", "Settings");
        model.addAttribute("currentPath", request.getRequestURI());
        return "admin/settings";
    }
}

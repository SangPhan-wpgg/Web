package vn.iotstar.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.iotstar.entity.Category;
import vn.iotstar.entity.Role;
import vn.iotstar.entity.User;
import vn.iotstar.entity.Video;
import vn.iotstar.service.CategoryService;
import vn.iotstar.service.RoleService;
import vn.iotstar.service.UserService;
import vn.iotstar.service.VideoService;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleService roleService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private VideoService videoService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Tạo role mặc định nếu chưa có
        createDefaultRoles();
        // Tạo tài khoản admin mặc định
        createDefaultAdmin();
        // Tạo categories mặc định
        createDefaultCategories();
        // Tạo videos mặc định
        createDefaultVideos();
    }

    private void createDefaultRoles() {
        // Tạo role ADMIN
        Role adminRole = roleService.findByName("ADMIN");
        if (adminRole == null) {
            adminRole = new Role();
            adminRole.setRoleName("ADMIN");
            roleService.save(adminRole);
            System.out.println("Created ADMIN role");
        }

        // Tạo role USER
        Role userRole = roleService.findByName("USER");
        if (userRole == null) {
            userRole = new Role();
            userRole.setRoleName("USER");
            roleService.save(userRole);
            System.out.println("Created USER role");
        }
    }
    
    private void createDefaultAdmin() {
        // Kiểm tra xem đã có admin chưa
        User existingAdmin = userService.findByUserName("admin");
        if (existingAdmin == null) {
            // Tạo tài khoản admin
            User admin = new User();
            admin.setUserName("admin");
            admin.setEmail("admin@example.com");
            admin.setFullName("Administrator");
            admin.setPhone("0123456789");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setCreatedDate(new java.util.Date());
            admin.setAvatar("https://ui-avatars.com/api/?name=Administrator&background=667eea&color=fff&size=128");
            
            // Gán role ADMIN
            Role adminRole = roleService.findByName("ADMIN");
            admin.setRole(adminRole);
            
            userService.save(admin);
            System.out.println("Created default admin account:");
            System.out.println("Username: admin");
            System.out.println("Password: admin123");
        }
        
        // Tạo user thường để test
        User existingUser = userService.findByUserName("user");
        if (existingUser == null) {
            User user = new User();
            user.setUserName("user");
            user.setEmail("user@example.com");
            user.setFullName("Nguyễn Văn A");
            user.setPhone("0987654321");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setCreatedDate(new java.util.Date());
            // Không set avatar để test placeholder
            
            Role userRole = roleService.findByName("USER");
            System.out.println("Found USER role: " + userRole);
            if (userRole == null) {
                System.out.println("USER role not found, creating it...");
                userRole = new Role();
                userRole.setRoleName("USER");
                userRole = roleService.save(userRole);
                System.out.println("Created USER role: " + userRole);
            }
            user.setRole(userRole);
            
            userService.save(user);
            System.out.println("Created default user account:");
            System.out.println("Username: user");
            System.out.println("Password: user123");
            System.out.println("Role: " + user.getRole().getRoleName());
        }
        
        // Tạo thêm user test khác
        User existingTestUser = userService.findByUserName("testuser");
        if (existingTestUser == null) {
            User testUser = new User();
            testUser.setUserName("testuser");
            testUser.setEmail("testuser@example.com");
            testUser.setFullName("Nguyễn Thị B");
            testUser.setPhone("0123456780");
            testUser.setPassword(passwordEncoder.encode("123456"));
            testUser.setCreatedDate(new java.util.Date());
            testUser.setAvatar("https://ui-avatars.com/api/?name=Nguyen+Thi+B&background=10b981&color=fff&size=128");
            
            Role userRole = roleService.findByName("USER");
            if (userRole == null) {
                userRole = new Role();
                userRole.setRoleName("USER");
                userRole = roleService.save(userRole);
            }
            testUser.setRole(userRole);
            
            userService.save(testUser);
            System.out.println("Created test user account:");
            System.out.println("Username: testuser");
            System.out.println("Password: 123456");
            System.out.println("Role: " + testUser.getRole().getRoleName());
        }
    }
    
    private void createDefaultCategories() {
        try {
            System.out.println("=== Creating default categories ===");
            // Tạo categories mặc định nếu chưa có
            String[] categoryNames = {
                "Công nghệ", "Giáo dục", "Giải trí", "Thể thao", "Âm nhạc",
                "Du lịch", "Ẩm thực", "Thời trang", "Làm đẹp", "Sức khỏe",
                "Kinh doanh", "Tài chính", "Bất động sản", "Xe cộ", "Điện tử"
            };
            String[] descriptions = {
                "Các video về công nghệ, lập trình, và phát triển phần mềm",
                "Video giáo dục, hướng dẫn học tập và kỹ năng",
                "Nội dung giải trí, phim ảnh và chương trình truyền hình",
                "Video thể thao, bóng đá, bóng rổ và các môn thể thao khác",
                "Video âm nhạc, ca nhạc và các chương trình âm nhạc",
                "Video du lịch, khám phá và trải nghiệm",
                "Video ẩm thực, nấu ăn và đánh giá món ăn",
                "Video thời trang, phong cách và xu hướng",
                "Video làm đẹp, chăm sóc da và trang điểm",
                "Video sức khỏe, thể dục và lối sống lành mạnh",
                "Video kinh doanh, khởi nghiệp và quản lý",
                "Video tài chính, đầu tư và quản lý tiền bạc",
                "Video bất động sản, mua bán nhà đất",
                "Video xe cộ, đánh giá và so sánh xe",
                "Video điện tử, công nghệ và gadget"
            };
            
            for (int i = 0; i < categoryNames.length; i++) {
                System.out.println("Checking category: " + categoryNames[i]);
                Category existingCategory = categoryService.findByCategoryName(categoryNames[i]);
                if (existingCategory == null) {
                    System.out.println("Creating new category: " + categoryNames[i]);
                    Category category = new Category();
                    category.setCategoryName(categoryNames[i]);
                    category.setDescription(descriptions[i]);
                    categoryService.save(category);
                    System.out.println("Successfully created category: " + categoryNames[i]);
                } else {
                    System.out.println("Category already exists: " + categoryNames[i]);
                }
            }
            System.out.println("=== Finished creating default categories ===");
        } catch (Exception e) {
            System.err.println("Error creating default categories: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createDefaultVideos() {
        try {
            System.out.println("=== Starting to create default videos ===");
            
            // Lấy admin user
            User admin = userService.findByUserName("admin");
            if (admin == null) {
                System.out.println("Admin user not found, skipping video creation");
                return;
            }
            
            // Lấy categories
            List<Category> categories = categoryService.findAll();
            if (categories.isEmpty()) {
                System.out.println("No categories found, skipping video creation");
                return;
            }
            
            // Tạo videos mẫu
            String[] videoTitles = {
                "Hướng dẫn lập trình Java cơ bản",
                "Spring Boot từ A đến Z",
                "Thiết kế giao diện với Bootstrap",
                "Cơ sở dữ liệu MySQL",
                "API RESTful với Spring",
                "React.js cho người mới bắt đầu",
                "Node.js và Express Framework",
                "MongoDB cơ bản",
                "Docker containerization",
                "AWS Cloud Computing",
                "Machine Learning với Python",
                "Data Science và Analytics",
                "Mobile App Development",
                "Game Development Unity",
                "Cybersecurity Fundamentals"
            };
            
            String[] videoDescriptions = {
                "Video hướng dẫn lập trình Java từ cơ bản đến nâng cao",
                "Học Spring Boot framework một cách chi tiết và dễ hiểu",
                "Thiết kế giao diện web đẹp với Bootstrap 5",
                "Quản lý cơ sở dữ liệu MySQL hiệu quả",
                "Xây dựng API RESTful với Spring Framework",
                "Học React.js từ cơ bản đến nâng cao",
                "Phát triển backend với Node.js và Express",
                "Làm việc với cơ sở dữ liệu NoSQL MongoDB",
                "Containerization và deployment với Docker",
                "Cloud computing và AWS services",
                "Machine Learning và AI với Python",
                "Phân tích dữ liệu và Data Science",
                "Phát triển ứng dụng di động",
                "Tạo game với Unity Engine",
                "Bảo mật thông tin và cybersecurity"
            };
            
            for (int i = 0; i < videoTitles.length; i++) {
                try {
                    // Kiểm tra xem video đã tồn tại chưa
                    List<Video> existingVideos = videoService.searchByTitle(videoTitles[i]);
                    if (existingVideos.isEmpty()) {
                        Video video = new Video();
                        video.setTitle(videoTitles[i]);
                        video.setDescription(videoDescriptions[i]);
                        video.setVideoCode("VID_" + System.currentTimeMillis() + "_" + i); // Tạo unique video code
                        video.setViews((int)(Math.random() * 1000)); // Random views
                        video.setActive(true);
                        video.setUser(admin);
                        video.setCategory(categories.get(i % categories.size())); // Phân bổ category
                        video.setDuration((int)(Math.random() * 3600) + 300); // Random duration 5-65 minutes
                        
                        videoService.save(video);
                        System.out.println("Successfully created video: " + videoTitles[i]);
                    } else {
                        System.out.println("Video already exists: " + videoTitles[i]);
                    }
                } catch (Exception e) {
                    System.err.println("Error creating video '" + videoTitles[i] + "': " + e.getMessage());
                    // Tiếp tục với video tiếp theo
                }
            }
            System.out.println("=== Finished creating default videos ===");
        } catch (Exception e) {
            System.err.println("Error creating default videos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

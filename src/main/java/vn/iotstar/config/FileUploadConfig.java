package vn.iotstar.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Cấu hình để serve static files từ thư mục uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        // Cấu hình cho video files (serve từ uploads/ để match với database path)
        registry.addResourceHandler("/video-files/**")
                .addResourceLocations("file:" + uploadDir + "/");
        
        // Cấu hình cho avatar files
        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:" + uploadDir + "/avatars/");
        
        // Cấu hình cho image files
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + uploadDir + "/images/");
    }
}

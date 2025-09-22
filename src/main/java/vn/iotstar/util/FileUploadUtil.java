package vn.iotstar.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

public class FileUploadUtil {

	private static final String UPLOAD_DIR = "uploads";

	public static String saveFile(MultipartFile file, String subDir) throws IOException {
		if (file.isEmpty()) {
			return null;
		}

		// Tạo thư mục nếu chưa tồn tại
		Path uploadPath = Paths.get(UPLOAD_DIR, subDir);
		if (!Files.exists(uploadPath)) {
			Files.createDirectories(uploadPath);
		}

		// Tạo tên file unique
		String originalFilename = file.getOriginalFilename();
		String fileExtension = "";
		if (originalFilename != null && originalFilename.contains(".")) {
			fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
		}
		String uniqueFilename = UUID.randomUUID().toString() + fileExtension;

		// Lưu file
		Path filePath = uploadPath.resolve(uniqueFilename);
		Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

		// Trả về đường dẫn relative
		return subDir + "/" + uniqueFilename;
	}

	public static boolean deleteFile(String filePath) {
		try {
			Path path = Paths.get(UPLOAD_DIR, filePath);
			return Files.deleteIfExists(path);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static String getFileExtension(String filename) {
		if (filename == null || !filename.contains(".")) {
			return "";
		}
		return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
	}

	public static boolean isVideoFile(String filename) {
		String extension = getFileExtension(filename);
		return extension.equals("mp4") || extension.equals("avi") || extension.equals("mov") || extension.equals("wmv")
				|| extension.equals("flv") || extension.equals("webm");
	}

	public static boolean isImageFile(String filename) {
		String extension = getFileExtension(filename);
		return extension.equals("jpg") || extension.equals("jpeg") || extension.equals("png") || extension.equals("gif")
				|| extension.equals("bmp") || extension.equals("webp");
	}
}

package vn.iotstar.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "videos")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;   // Khóa chính tự tăng

    @Column(name = "video_code", length = 50, unique = true, nullable = false)
    private String videoCode = "";   // Mã video (ví dụ YouTube ID)

    @Column(name = "title", columnDefinition = "NVARCHAR(255)", nullable = false)
    private String title;   // Tiêu đề

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;   // Mô tả

    @Column(name = "poster", columnDefinition = "NVARCHAR(255)")
    private String poster;   // Ảnh poster (URL hoặc đường dẫn)

    @Column(name = "views")
    private int views = 0;   // Lượt xem

    @Column(name = "active")
    private boolean active = true;   // Trạng thái

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date")
    private Date createdDate = new Date();   // Ngày tạo
    
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "video_file", columnDefinition = "NVARCHAR(255)")
    private String videoFile;   // Đường dẫn file video

    @Column(name = "duration")
    private int duration = 0;   // Thời lượng video (giây)

    public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public String getVideoFile() {
        return videoFile;
    }
    public void setVideoFile(String videoFile) {
        this.videoFile = videoFile;
    }

    public int getDuration() {
        return duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
	// --- Getter & Setter ---
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getVideoCode() {
        return videoCode;
    }
    public void setVideoCode(String videoCode) {
        this.videoCode = videoCode;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getViews() {
        return views;
    }
    public void setViews(int views) {
        this.views = views;
    }

    public boolean isActive() {
        return active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
}

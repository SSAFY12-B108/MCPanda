package SSAFY_B108.MCPanda.domain.example.dto;
    
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 사용자 정보를 담는 DTO 클래스
 */
@Schema(description = "사용자 정보 DTO")
public class UserDto {
    @Schema(description = "사용자 ID", example = "user123")
    private String id;
    
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;
    
    @Schema(description = "사용자 이메일", example = "hong@example.com")
    private String email;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImage;

    // 기본 생성자
    public UserDto() {
    }

    // 모든 필드 초기화 생성자
    public UserDto(String id, String name, String email, String profileImage) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
    }

    // Getter와 Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
} 
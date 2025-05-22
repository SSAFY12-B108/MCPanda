package SSAFY_B108.MCPanda.domain.member.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap; // HashMap 임포트 추가
import java.util.List;
import java.util.Map; // Map 임포트 추가

/**
 * 회원 정보 엔티티
 * MongoDB의 Members 컬렉션과 매핑됩니다.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "members")
public class Member {

    @Id
    private ObjectId id; // MongoDB ObjectId 타입 사용

    private String name;

    @Indexed(unique = true)
    private String email;

    private String nickname;

    @Field("profile_image")
    private String profileImage;

    // 사용자 역할 (기본값 USER)
    @Builder.Default
    private String role = "USER";

    // OAuth2 제공자 정보 (Key: provider 이름, Value: provider 고유 ID)
    @Field("oauth_ids")
    @Builder.Default
    private Map<String, String> oauthIds = new HashMap<>();

    // 생성 일시
    @CreatedDate
    @Field("created_at")
    private LocalDateTime createdAt;

    // 수정 일시
    @LastModifiedDate
    @Field("updated_at")
    private LocalDateTime updatedAt;

    // 좋아요한 게시글 목록
    @Builder.Default
    @Field("liked_posts")
    private List<ObjectId> likedPosts = new ArrayList<>();

    /**
     * 회원 정보 업데이트 (OAuth2 로그인 시 사용)
     */
    public void update(String name, String profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }

    /**
     * OAuth2 제공자 정보 추가 (계정 연동 시 사용)
     */
    public void addOAuthId(String provider, String providerId) {
        if (this.oauthIds == null) {
            this.oauthIds = new HashMap<>();
        }
        this.oauthIds.put(provider, providerId);
    }

    /**
     * 닉네임 업데이트
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * OAuth 연동 정보 모두 제거
     */
    public void clearOAuthIds() {
        if (this.oauthIds != null) {
            this.oauthIds.clear();
        }
    }

    /**
     * 게시글 좋아요 추가
     */
    public void addLikedPost(ObjectId postId) {
        if (this.likedPosts == null) {
            this.likedPosts = new ArrayList<>();
        }
        if (!this.likedPosts.contains(postId)) {
            this.likedPosts.add(postId);
        }
    }

    /**
     * 게시글 좋아요 취소
     */
    public void removeLikedPost(ObjectId postId) {
        if (this.likedPosts != null) {
            this.likedPosts.remove(postId);
        }
    }

    /**
     * 게시글 좋아요 여부 확인
     */
    public boolean hasLikedPost(ObjectId postId) {
        return this.likedPosts != null && this.likedPosts.contains(postId);
    }
}

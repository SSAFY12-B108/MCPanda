package SSAFY_B108.MCPanda.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * RefreshToken을 저장하는 MongoDB 도큐먼트 엔티티
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {
    @Id
    private String id;

    // 사용자 ID
    @Indexed
    private String memberId; // Member의 MongoDB ObjectId를 문자열로 저장

    // RefreshToken 값
    @Indexed(unique = true)
    private String token;

    // 만료 시간 - TTL 인덱스는 MongoTTLConfig에서 따로 설정
    private Instant expiryDate;

    private Instant createdAt;

    private Instant updatedAt;

    @Builder
    public RefreshToken(String memberId, String token, Instant expiryDate) {
        this.memberId = memberId;
        this.token = token;
        this.expiryDate = expiryDate;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * 토큰 값 업데이트
     */
    public void updateToken(String token) {
        this.token = token;
        this.updatedAt = Instant.now();
    }

    /**
     * 만료 시간 업데이트
     */
    public void updateExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
        this.updatedAt = Instant.now();
    }
}
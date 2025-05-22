package SSAFY_B108.MCPanda.domain.auth.repository;

import SSAFY_B108.MCPanda.domain.auth.entity.RefreshToken;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * RefreshToken 엔티티를 위한 MongoDB 레포지토리
 */
public interface RefreshTokenRepository extends MongoRepository<RefreshToken, String> {
    /**
     * 토큰 값으로 RefreshToken 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 사용자 ID로 RefreshToken 조회
     */
    Optional<RefreshToken> findByMemberId(String memberId);

    /**
     * 사용자 ID로 RefreshToken 삭제
     */
    void deleteByMemberId(String memberId);
}
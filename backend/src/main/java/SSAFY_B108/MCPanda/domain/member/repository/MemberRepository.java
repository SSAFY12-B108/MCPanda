package SSAFY_B108.MCPanda.domain.member.repository;

import SSAFY_B108.MCPanda.domain.member.entity.Member;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 회원 정보 레포지토리
 */
public interface MemberRepository extends MongoRepository<Member, ObjectId> {

    /**
     * 이메일로 회원 조회 (삭제되지 않은 회원만)
     */
    @Query("{ 'email': ?0, 'deleted_at': null }")
    Optional<Member> findByEmail(String email);

    /**
     * 닉네임 중복 확인
     */
    @Query(value = "{ 'nickname': ?0, 'deleted_at': null }", count = true)
    long countByNickname(String nickname);

    /**
     * 특정 게시글을 좋아요한 회원 목록 조회
     */
    @Query("{ 'liked_posts': ?0, 'deleted_at': null }")
    List<Member> findByLikedPostsContaining(ObjectId postId);

}
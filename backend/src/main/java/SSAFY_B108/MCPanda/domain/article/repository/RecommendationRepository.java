package SSAFY_B108.MCPanda.domain.article.repository;

import SSAFY_B108.MCPanda.domain.article.entity.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository // 이 인터페이스가 데이터 접근 계층의 컴포넌트(Bean)임을 Spring에게 알립니다.
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {

    // memberId와 articleId를 기준으로 Recommendation 객체를 찾는 메소드
    // 이 메소드를 이용해 특정 사용자가 특정 게시물을 이미 추천했는지 확인할 수 있습니다.
    Optional<Recommendation> findByMemberIdAndArticleId(String memberId, String articleId);

    // articleId를 기준으로 해당 게시글에 대한 모든 추천 기록을 삭제하는 메소드
    // 예를 들어, 게시글이 삭제될 때 이 게시글에 달린 모든 추천 기록도 함께 지울 때 사용합니다.
    void deleteByArticleId(String articleId);

    // (추가 선택 사항) memberId를 기준으로 해당 사용자의 모든 추천 기록을 삭제하는 메소드
    // 예를 들어, 회원 탈퇴 시 그 회원이 남긴 모든 추천 기록을 지울 때 사용할 수 있습니다.
    // void deleteByMemberId(String memberId);

    // (추가 선택 사항) articleId를 기준으로 해당 게시글의 총 추천 수를 세는 메소드
    // Article 엔티티의 recommendCount와 별도로, 필요하다면 이 방법으로도 개수를 셀 수 있습니다.
    // long countByArticleId(String articleId);
}

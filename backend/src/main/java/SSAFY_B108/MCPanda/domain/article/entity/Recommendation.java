package SSAFY_B108.MCPanda.domain.article.entity;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "recommendations") // MongoDB에 "recommendations" 라는 이름의 컬렉션으로 저장됩니다.
// memberId와 articleId의 조합이 중복되지 않도록 유니크 인덱스를 설정합니다.
// 이렇게 하면 한 사용자가 같은 게시글을 여러 번 추천하는 것을 방지할 수 있습니다.
@CompoundIndex(name = "member_article_unique", def = "{'memberId' : 1, 'articleId' : 1}", unique = true)
public class Recommendation {

    @Id // 이 필드가 MongoDB 문서의 고유 ID(_id)임을 나타냅니다.
    private String id;

    private String memberId; // 추천한 사용자의 ID (Member 엔티티의 ID)

    private String articleId; // 추천된 게시글의 ID (Article 엔티티의 ID)

    @CreatedDate // 문서가 생성될 때 자동으로 현재 시간이 기록됩니다. (Auditing 설정 필요)
                 // 만약 Auditing 설정을 사용하지 않는다면, 이 어노테이션은 빼고 서비스에서 직접 시간을 설정해야 합니다.
                 // 지금은 일단 넣어두고, 나중에 필요하면 Auditing 설정을 하거나 수동으로 시간을 넣도록 수정할게요.
    private LocalDateTime createdAt;

    // JPA/Spring Data를 사용하려면 기본 생성자가 필요합니다.
    public Recommendation() {
    }

    // 추천 정보를 생성할 때 사용할 생성자
    public Recommendation(String memberId, String articleId) {
        this.memberId = memberId;
        this.articleId = articleId;
    }

    // Getter와 Setter 메소드들
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

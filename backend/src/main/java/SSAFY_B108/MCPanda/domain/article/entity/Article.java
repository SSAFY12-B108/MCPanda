package SSAFY_B108.MCPanda.domain.article.entity; // 패키지 경로를 다시 한번 확인해주세요.

import org.springframework.data.annotation.CreatedDate; // 생성 시간 자동 관리용
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field; // 필드명 명시적 지정 시 사용

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set; // mcps를 위해 Set 사용

@Document(collection = "articles") // 이 클래스가 MongoDB의 "articles" 컬렉션에 매핑됨을 나타냅니다.
public class Article {

    @Id // 이 필드가 MongoDB 문서의 고유 ID(_id)임을 나타냅니다.
    private String id; // MongoDB의 ObjectId는 보통 String으로 다룹니다.

    // @Field("article_title") // MongoDB에 저장될 필드 이름을 "article_title"로 지정하고 싶을 때 사용
    private String title; // 게시글 제목

    @Field("isNotice") // MongoDB에 저장될 필드 이름을 "isNotice"로 명시 (Java 변수명과 동일하지만 명시)
    private boolean isNotice; // 공지 여부

    private String content; // 게시글 내용

    private Set<String> mcps; // 예: {"MCP_A", "MCP_B"} 같은 태그 저장

    private Author author; // 위에서 만든 Author 클래스 타입

    // @CreatedDate // MongoDB Auditing 기능을 활성화하면 자동으로 생성 시점의 날짜가 들어감
    private LocalDateTime createdAt; // Auditing 사용 안 할 경우 수동 또는 서비스 로직에서 설정

    private int recommendCount;

    private List<Comment> comments; // 위에서 만든 Comment 클래스의 리스트

    // --- Lombok 사용하지 않는 경우 필요한 Getter/Setter ---
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isNotice() { // boolean 타입의 getter는 보통 isXXX() 형태
        return isNotice;
    }

    public void setNotice(boolean notice) {
        isNotice = notice;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<String> getMcps() {
        return mcps;
    }

    public void setMcps(Set<String> mcps) {
        this.mcps = mcps;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    // 기본 생성자
    public Article() {
    }

    // 모든 필드를 사용하는 생성자 (선택 사항)
    public Article(String title, boolean isNotice, String content, Set<String> mcps, Author author, LocalDateTime createdAt, int recommendCount, List<Comment> comments) {
        this.title = title;
        this.isNotice = isNotice;
        this.content = content;
        this.mcps = mcps;
        this.author = author;
        this.createdAt = createdAt;
        this.recommendCount = recommendCount;
        this.comments = comments;
    }
}
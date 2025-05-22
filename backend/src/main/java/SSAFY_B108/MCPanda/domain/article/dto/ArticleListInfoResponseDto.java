package SSAFY_B108.MCPanda.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map; // Article 엔티티의 mcps가 Map<String, Object>이므로 사용

@Schema(description = "게시글 목록 조회 시 개별 게시글 정보 DTO")
public class ArticleListInfoResponseDto {

    @Schema(description = "게시글 ID", example = "6635c1f2a9b8b3a7d0c12345")
    private String id; // API 명세의 _id에 해당

    @Schema(description = "공지글 여부", example = "true")
    private boolean isNotice;

    @Schema(description = "게시글 제목", example = "추천 게시글 예시")
    private String title;

    @Schema(description = "게시글 내용 (목록용)", example = "이 게시글은 예시입니다.")
    private String content;

    @Schema(description = "MCP 설정", example = "{\"AWS\": {...}, \"Notion\": {...}}")
    private Map<String, Object> mcps;

    @Schema(description = "작성일시", example = "2025-05-03T14:20:00Z")
    private String createdAt; // LocalDateTime을 String으로 변환하여 전달 예정

    @Schema(description = "작성자 정보")
    private AuthorDto author; // 위에서 만든 AuthorDto 사용

    @Schema(description = "추천 수", example = "21")
    private int recommendCount;

    @Schema(description = "댓글 수", example = "3")
    private int commentsCount;

    // 기본 생성자
    public ArticleListInfoResponseDto() {
    }

    // 모든 필드를 받는 생성자 (빌더 패턴 대신 사용 가능)
    public ArticleListInfoResponseDto(String id, boolean isNotice, String title, String content, Map<String, Object> mcps, String createdAt, AuthorDto author, int recommendCount, int commentsCount) {
        this.id = id;
        this.isNotice = isNotice;
        this.title = title;
        this.content = content;
        this.mcps = mcps;
        this.createdAt = createdAt;
        this.author = author;
        this.recommendCount = recommendCount;
        this.commentsCount = commentsCount;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isNotice() { return isNotice; } // boolean getter는 isXXX()
    public void setNotice(boolean notice) { isNotice = notice; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Map<String, Object> getMcps() { return mcps; }
    public void setMcps(Map<String, Object> mcps) { this.mcps = mcps; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public AuthorDto getAuthor() { return author; }
    public void setAuthor(AuthorDto author) { this.author = author; }

    public int getRecommendCount() { return recommendCount; }
    public void setRecommendCount(int recommendCount) { this.recommendCount = recommendCount; }

    public int getCommentsCount() { return commentsCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
}

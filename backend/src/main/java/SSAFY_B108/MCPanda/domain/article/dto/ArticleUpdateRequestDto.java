package SSAFY_B108.MCPanda.domain.article.dto;

public class ArticleUpdateRequestDto {
    private String title;
    private String content;

    // 기본 생성자
    public ArticleUpdateRequestDto() {
    }

    // 모든 필드를 받는 생성자
    public ArticleUpdateRequestDto(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // Getter와 Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

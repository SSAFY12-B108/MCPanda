package SSAFY_B108.MCPanda.domain.article.dto;

import java.util.Set;

public class ArticleUpdateRequestDto {
    private String title;
    private String content;
    private Set<String> mcps;

    // 기본 생성자
    public ArticleUpdateRequestDto() {
    }

    // 모든 필드를 받는 생성자
    public ArticleUpdateRequestDto(String title, String content, Set<String> mcps) {
        this.title = title;
        this.content = content;
        this.mcps = mcps;
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

    public Set<String> getMcps() {
        return mcps;
    }

    public void setMcps(Set<String> mcps) {
        this.mcps = mcps;
    }
}

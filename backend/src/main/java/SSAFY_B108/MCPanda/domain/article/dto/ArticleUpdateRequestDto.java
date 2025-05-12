package SSAFY_B108.MCPanda.domain.article.dto;

import java.util.Map;

public class ArticleUpdateRequestDto {
    private String title;
    private String content;
    private Map<String, Object> mcps;

    // 기본 생성자
    public ArticleUpdateRequestDto() {
    }

    // 모든 필드를 받는 생성자
    public ArticleUpdateRequestDto(String title, String content, Map<String, Object> mcps) {
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
    
    public Map<String, Object> getMcps() {
        return mcps;
    }
    
    public void setMcps(Map<String, Object> mcps) {
        this.mcps = mcps;
    }
}

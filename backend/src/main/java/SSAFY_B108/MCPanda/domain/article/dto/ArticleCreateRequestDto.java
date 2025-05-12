    package SSAFY_B108.MCPanda.domain.article.dto; // 실제 패키지 경로를 확인해주세요.

    import java.util.Set; // mcps를 Set으로 받기 위해 import

    public class ArticleCreateRequestDto {

        private String title;
        private String content;
        private Set<String> mcps; // API 명세에 따라 MCP 태그들을 Set으로 받습니다.

        // Lombok을 사용하지 않으므로 Getter가 필요합니다.
        // Setter는 Controller에서 요청 바디를 이 DTO로 매핑할 때 Jackson 라이브러리가 사용하므로 필요할 수 있습니다.
        // 또는 생성자를 통해 값을 받을 수도 있습니다. 지금은 Getter와 기본 생성자, 모든 필드 생성자를 추가해볼게요.

        public ArticleCreateRequestDto() {
        }

        public ArticleCreateRequestDto(String title, String content, Set<String> mcps) {
            this.title = title;
            this.content = content;
            this.mcps = mcps;
        }

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
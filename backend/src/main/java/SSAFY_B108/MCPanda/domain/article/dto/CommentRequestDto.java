package SSAFY_B108.MCPanda.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 작성 요청 DTO")
public class CommentRequestDto {

    @Schema(description = "댓글 내용", example = "정말 유익한 글이네요!", required = true)
    private String content;

    // 기본 생성자
    public CommentRequestDto() {
    }

    // 모든 필드를 받는 생성자
    public CommentRequestDto(String content) {
        this.content = content;
    }

    // Getter와 Setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
} 
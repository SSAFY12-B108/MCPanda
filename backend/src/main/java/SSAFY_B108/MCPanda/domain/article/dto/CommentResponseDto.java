package SSAFY_B108.MCPanda.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "댓글 응답 DTO")
public class CommentResponseDto {

    @Schema(description = "댓글 ID", example = "60c72b2f9b1d8c1f7c8e4f2a")
    private String commentId;

    @Schema(description = "처리 결과 메시지", example = "댓글이 성공적으로 작성되었습니다.")
    private String message;

    // 기본 생성자
    public CommentResponseDto() {
    }

    // 모든 필드를 받는 생성자
    public CommentResponseDto(String commentId, String message) {
        this.commentId = commentId;
        this.message = message;
    }

    // Getter와 Setter
    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 
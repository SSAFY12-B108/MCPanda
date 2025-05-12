package SSAFY_B108.MCPanda.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 추천/추천 취소 응답 DTO")
public class ArticleRecommendResponseDto {

    @Schema(description = "처리 결과 메시지", example = "추천 완료")
    private String message;

    @Schema(description = "게시글의 현재 총 추천 수", example = "13")
    private int recommendCount;

    @Schema(description = "현재 사용자의 해당 게시글 추천 상태", example = "true")
    private boolean isLiked;

    // 기본 생성자
    public ArticleRecommendResponseDto() {
    }

    // 모든 필드를 받는 생성자
    public ArticleRecommendResponseDto(String message, int recommendCount, boolean isLiked) {
        this.message = message;
        this.recommendCount = recommendCount;
        this.isLiked = isLiked;
    }

    // Getter와 Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getRecommendCount() {
        return recommendCount;
    }

    public void setRecommendCount(int recommendCount) {
        this.recommendCount = recommendCount;
    }

    public boolean isLiked() { // boolean 타입의 getter는 isXXX() 형태
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}

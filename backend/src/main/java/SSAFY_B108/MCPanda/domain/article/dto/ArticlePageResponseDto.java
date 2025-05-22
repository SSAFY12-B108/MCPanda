package SSAFY_B108.MCPanda.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "게시글 목록 페이징 응답 DTO")
public class ArticlePageResponseDto {

    @Schema(description = "현재 페이지 번호", example = "1")
    private int page;

    @Schema(description = "전체 페이지 수", example = "5")
    private int totalPages;

    @Schema(description = "전체 게시글 수", example = "42")
    private long totalArticles; // 게시글 수가 많을 수 있으므로 long 타입

    @Schema(description = "현재 페이지의 게시글 목록")
    private List<ArticleListInfoResponseDto> articles; // 위에서 만든 ArticleListInfoResponseDto의 리스트

    // 기본 생성자
    public ArticlePageResponseDto() {
    }

    // 모든 필드를 받는 생성자
    public ArticlePageResponseDto(int page, int totalPages, long totalArticles, List<ArticleListInfoResponseDto> articles) {
        this.page = page;
        this.totalPages = totalPages;
        this.totalArticles = totalArticles;
        this.articles = articles;
    }

    // Getters and Setters
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getTotalArticles() { return totalArticles; }
    public void setTotalArticles(long totalArticles) { this.totalArticles = totalArticles; }

    public List<ArticleListInfoResponseDto> getArticles() { return articles; }
    public void setArticles(List<ArticleListInfoResponseDto> articles) { this.articles = articles; }
}

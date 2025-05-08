package SSAFY_B108.MCPanda.domain.board.dto;

import SSAFY_B108.MCPanda.domain.board.entity.Article;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArticleListResponseDto {
    private int page;
    private int totalPages;
    private int totalArticles;
    private List<ArticleResponseDto> articles;
    
    public static ArticleListResponseDto of(List<Article> articles, int page, int totalPages, int totalArticles) {
        return ArticleListResponseDto.builder()
                .page(page)
                .totalPages(totalPages)
                .totalArticles(totalArticles)
                .articles(articles.stream()
                          .map(ArticleResponseDto::brief)
                          .collect(Collectors.toList()))
                .build();
    }
} 
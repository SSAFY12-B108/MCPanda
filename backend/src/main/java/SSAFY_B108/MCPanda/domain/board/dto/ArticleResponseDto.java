package SSAFY_B108.MCPanda.domain.board.dto;

import SSAFY_B108.MCPanda.domain.board.entity.Article;
import SSAFY_B108.MCPanda.domain.board.entity.Author;
import SSAFY_B108.MCPanda.domain.board.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ArticleResponseDto {
    private String id;
    private String title;
    private String content;
    private List<String> mcps;
    private String category;
    private AuthorDto author;
    private int recommendCount;
    private boolean isNotice;
    private LocalDateTime createdAt;
    private List<CommentDto> comments;
    private int commentsCount;
    
    public static ArticleResponseDto from(Article article) {
        return ArticleResponseDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .mcps(article.getMcps())
                .category(article.getCategory())
                .author(AuthorDto.from(article.getAuthor()))
                .recommendCount(article.getRecommendCount())
                .isNotice(article.isNotice())
                .createdAt(article.getCreatedAt())
                .comments(article.getComments().stream()
                          .map(CommentDto::from)
                          .collect(Collectors.toList()))
                .commentsCount(article.getComments().size())
                .build();
    }
    
    // 목록 조회용 간략 정보만 포함
    public static ArticleResponseDto brief(Article article) {
        return ArticleResponseDto.builder()
                .id(article.getId())
                .title(article.getTitle())
                .content(article.getContent())
                .mcps(article.getMcps())
                .author(AuthorDto.from(article.getAuthor()))
                .recommendCount(article.getRecommendCount())
                .commentsCount(article.getComments().size())
                .isNotice(article.isNotice())
                .createdAt(article.getCreatedAt())
                .build();
    }
    
    @Getter
    @Builder
    public static class AuthorDto {
        private String name;
        private String profileImage;
        
        public static AuthorDto from(Author author) {
            return AuthorDto.builder()
                    .name(author.getName())
                    .profileImage(author.getProfileImage())
                    .build();
        }
    }
    
    @Getter
    @Builder
    public static class CommentDto {
        private String id;
        private AuthorDto author;
        private String content;
        private LocalDateTime createdAt;
        
        public static CommentDto from(Comment comment) {
            return CommentDto.builder()
                    .id(comment.getId())
                    .author(AuthorDto.from(comment.getAuthor()))
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .build();
        }
    }
} 
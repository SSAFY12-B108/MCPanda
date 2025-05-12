package SSAFY_B108.MCPanda.domain.board.service;

import SSAFY_B108.MCPanda.domain.board.dto.ArticleListResponseDto;
import SSAFY_B108.MCPanda.domain.board.dto.ArticleRequestDto;
import SSAFY_B108.MCPanda.domain.board.dto.ArticleResponseDto;
import SSAFY_B108.MCPanda.domain.board.dto.CommentRequestDto;
import SSAFY_B108.MCPanda.domain.board.entity.Article;
import SSAFY_B108.MCPanda.domain.board.entity.Author;
import SSAFY_B108.MCPanda.domain.board.entity.Comment;
import SSAFY_B108.MCPanda.domain.board.repository.ArticleRepository;
import SSAFY_B108.MCPanda.global.exception.CustomException;
import SSAFY_B108.MCPanda.global.response.ApiStatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    
    // 게시글 목록 조회 (페이징, 검색)
    public ArticleListResponseDto findArticles(String search, String type, int page) {
        int pageSize = 10;
        Pageable pageable;
        
        if ("latest".equals(type)) {
            pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        } else { // "recommend"
            pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "recommendCount"));
        }
        
        Page<Article> articlePage;
        if (search != null && !search.trim().isEmpty()) {
            articlePage = articleRepository.findByContentContainingOrderByCreatedAtDesc(search, pageable);
        } else if ("recommend".equals(type)) {
            articlePage = articleRepository.findAllByOrderByRecommendCountDesc(pageable);
        } else {
            articlePage = articleRepository.findAll(pageable);
        }
        
        return ArticleListResponseDto.of(
                articlePage.getContent(),
                page,
                articlePage.getTotalPages(),
                (int) articlePage.getTotalElements()
        );
    }
    
    // 카테고리별 게시글 목록 조회
    public ArticleListResponseDto findArticlesByCategory(String category, int page) {
        int pageSize = 10;
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<Article> articlePage = articleRepository.findByCategoryOrderByCreatedAtDesc(category, pageable);
        
        return ArticleListResponseDto.of(
                articlePage.getContent(),
                page,
                articlePage.getTotalPages(),
                (int) articlePage.getTotalElements()
        );
    }
    
    // 게시글 상세 조회
    public ArticleResponseDto findArticle(String id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다: " + id));
        
        return ArticleResponseDto.from(article);
    }
    
    // 게시글 생성
    public ArticleResponseDto createArticle(ArticleRequestDto requestDto, String userId, String userName, String profileImage) {
        Author author = Author.builder()
                .id(userId)
                .name(userName)
                .profileImage(profileImage)
                .build();
        
        Article article = requestDto.toEntity(author, false); // 일반 게시글은 isNotice = false
        
        Article savedArticle = articleRepository.save(article);
        
        return ArticleResponseDto.from(savedArticle);
    }
    
    // 게시글 수정
    public ArticleResponseDto updateArticle(String id, ArticleRequestDto requestDto, String userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다: " + id));
        
        // 작성자 검증
        if (!article.getAuthor().getId().equals(userId)) {
            throw new CustomException(ApiStatusCode.FORBIDDEN, "게시글 수정 권한이 없습니다.");
        }
        
        article.update(requestDto.getTitle(), requestDto.getContent(), requestDto.getMcps(), requestDto.getCategory());
        
        Article updatedArticle = articleRepository.save(article);
        
        return ArticleResponseDto.from(updatedArticle);
    }
    
    // 게시글 삭제
    public void deleteArticle(String id, String userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다: " + id));
        
        // 작성자 검증
        if (!article.getAuthor().getId().equals(userId)) {
            throw new CustomException(ApiStatusCode.FORBIDDEN, "게시글 삭제 권한이 없습니다.");
        }
        
        articleRepository.delete(article);
    }
    
    // 게시글 추천
    public void recommendArticle(String id, String userId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다: " + id));
        
        // 자신의 게시글에는 추천할 수 없도록 검증 (선택사항)
        if (article.getAuthor().getId().equals(userId)) {
            throw new CustomException(ApiStatusCode.BAD_REQUEST, "자신의 게시글에는 추천할 수 없습니다.");
        }
        
        article.increaseRecommendCount();
        articleRepository.save(article);
    }
    
    // 댓글 작성
    public ArticleResponseDto addComment(String articleId, CommentRequestDto requestDto, String userId, String userName, String profileImage) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다: " + articleId));
        
        Author author = Author.builder()
                .id(userId)
                .name(userName)
                .profileImage(profileImage)
                .build();
        
        Comment comment = requestDto.toEntity(author);
        
        article.addComment(comment);
        Article updatedArticle = articleRepository.save(article);
        
        return ArticleResponseDto.from(updatedArticle);
    }
    
    // 댓글 삭제
    public ArticleResponseDto deleteComment(String articleId, String commentId, String userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new NoSuchElementException("게시글을 찾을 수 없습니다: " + articleId));
        
        // 댓글이 존재하는지 확인
        boolean commentExists = article.getComments().stream()
                .anyMatch(comment -> comment.getId().equals(commentId));
                
        if (!commentExists) {
            throw new NoSuchElementException("댓글을 찾을 수 없습니다: " + commentId);
        }
        
        // 댓글 작성자 확인
        boolean isCommentAuthor = article.getComments().stream()
                .anyMatch(comment -> comment.getId().equals(commentId) && comment.getAuthor().getId().equals(userId));
                
        if (!isCommentAuthor) {
            throw new CustomException(ApiStatusCode.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }
        
        article.removeComment(commentId);
        Article updatedArticle = articleRepository.save(article);
        
        return ArticleResponseDto.from(updatedArticle);
    }
} 
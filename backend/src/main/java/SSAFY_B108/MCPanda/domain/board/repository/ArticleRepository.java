package SSAFY_B108.MCPanda.domain.board.repository;

import SSAFY_B108.MCPanda.domain.board.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {

    // 카테고리별 게시글 목록 조회 (페이지네이션)
    Page<Article> findByCategoryOrderByCreatedAtDesc(String category, Pageable pageable);
    
    // 검색어를 포함하는 게시글 목록 조회 (페이지네이션)
    @Query("{ 'content': { $regex: ?0, $options: 'i' } }")
    Page<Article> findByContentContainingOrderByCreatedAtDesc(String search, Pageable pageable);
    
    // 추천 수 기준 정렬 게시글 목록 조회 (페이지네이션)
    Page<Article> findAllByOrderByRecommendCountDesc(Pageable pageable);
    
    // 툴별 게시글 목록 조회
    @Query("{ 'mcps': ?0 }")
    Page<Article> findByMcpsContainingOrderByCreatedAtDesc(String mcp, Pageable pageable);
    
    // 작성자 ID로 게시글 목록 조회
    List<Article> findByAuthorIdOrderByCreatedAtDesc(String authorId);
    
    // ID로 게시글 조회
    Optional<Article> findById(String id);
} 
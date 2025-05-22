package SSAFY_B108.MCPanda.domain.article.repository;

import SSAFY_B108.MCPanda.domain.article.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
    
    // 제목이나 내용에 검색어를 포함하는 게시글 조회
    @Query("{ $or: [ {'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?0, $options: 'i'}} ] }")
    Page<Article> findByTitleOrContentContaining(String search, Pageable pageable);
    
    // MCP 이름으로 게시글 조회 (특정 MCP 이름이 mcps 맵에 키로 존재하는지)
    @Query("{ 'mcps.?0': { $exists: true } }")
    Page<Article> findByMcpName(String mcpName, Pageable pageable);
    
    // MCP 이름 목록 중 하나라도 포함하는 게시글 조회
    @Query("{ $or: [ ?0 ] }")
    Page<Article> findByMcpNameIn(List<String> mcpQueries, Pageable pageable);
    
    // 검색어와 MCP 이름 목록을 모두 적용하여 게시글 조회
    @Query("{ $and: [ " +
           "{ $or: [ {'title': {$regex: ?0, $options: 'i'}}, {'content': {$regex: ?0, $options: 'i'}} ] }, " +
           "{ $or: [ ?1 ] } " +
           "] }")
    Page<Article> findByTitleOrContentContainingAndMcpNameIn(String search, List<String> mcpQueries, Pageable pageable);
}

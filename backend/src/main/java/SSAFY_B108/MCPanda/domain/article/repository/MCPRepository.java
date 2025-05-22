package SSAFY_B108.MCPanda.domain.article.repository;

import SSAFY_B108.MCPanda.domain.article.entity.MCP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MCPRepository extends MongoRepository<MCP, String> {
    Optional<MCP> findByName(String name);
    
    // 카테고리로 MCP 목록 조회 (대소문자 무시)
    @Query("{ 'category': { $regex: ?0, $options: 'i' } }")
    List<MCP> findByCategoryIgnoreCase(String category);
}

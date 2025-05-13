package SSAFY_B108.MCPanda.domain.article.repository;

import SSAFY_B108.MCPanda.domain.article.entity.MCP;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MCPRepository extends MongoRepository<MCP, String> {
    Optional<MCP> findByName(String name);
}

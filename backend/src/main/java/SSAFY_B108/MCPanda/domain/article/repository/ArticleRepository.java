package SSAFY_B108.MCPanda.domain.article.repository;

import SSAFY_B108.MCPanda.domain.article.entity.Article;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends MongoRepository<Article, String> {
}

package SSAFY_B108.MCPanda.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MongoTTLConfig implements ApplicationRunner {

    private final MongoTemplate mongoTemplate;
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Index ttlIndex = new Index()
                .on("expiryDate", Sort.Direction.ASC)
                .expire(0); // expiryDate 시각 도달 시 삭제
        
        mongoTemplate.indexOps("refresh_tokens") // refreshToken 컬렉션명
                .ensureIndex(ttlIndex);
    }
}

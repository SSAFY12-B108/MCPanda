package SSAFY_B108.MCPanda.domain.board.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 게시글 카테고리 정보를 나타내는 엔티티
 */
@Document(collection = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    
    @Id
    private String id;
    
    private String name;
    
    private String description;
} 
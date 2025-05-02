package SSAFY_B108.MCPanda.domain.board.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 게시글 정보를 나타내는 엔티티
 */
@Document(collection = "posts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
    
    @Id
    private String id;
    
    private String title;
    
    private String content;
    
    @DBRef
    private Category category;
    
    @DBRef(lazy = true)
    private List<Comment> comments = new ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
    
    private String authorId;
    
    private String authorName;
    
    private int viewCount;
    
    private int likeCount;
} 
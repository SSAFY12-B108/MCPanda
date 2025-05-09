package SSAFY_B108.MCPanda.domain.board.entity;

import SSAFY_B108.MCPanda.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {

    @Id
    private String id;
    
    private String title;
    
    private String content;
    
    private List<String> mcps = new ArrayList<>();
    
    private String category;
    
    private Author author;
    
    private int recommendCount;
    
    private boolean isNotice;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Article(String title, String content, List<String> mcps, String category, 
                  Author author, boolean isNotice) {
        this.title = title;
        this.content = content;
        this.mcps = mcps != null ? mcps : new ArrayList<>();
        this.category = category;
        this.author = author;
        this.recommendCount = 0;
        this.isNotice = isNotice;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.comments = new ArrayList<>();
    }

    public void update(String title, String content, List<String> mcps, String category) {
        this.title = title;
        this.content = content;
        this.mcps = mcps;
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseRecommendCount() {
        this.recommendCount++;
    }

    public void decreaseRecommendCount() {
        if (this.recommendCount > 0) {
            this.recommendCount--;
        }
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void removeComment(String commentId) {
        this.comments.removeIf(comment -> comment.getId().equals(commentId));
    }
} 
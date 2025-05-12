package SSAFY_B108.MCPanda.domain.article.entity;

import org.springframework.data.annotation.Id;
import java.time.LocalDateTime;

public class Comment {

    @Id
    private String id;

    private Author author;
    private String content;
    private LocalDateTime createdAt;

    public Comment() {
    }

    public Comment(Author author, String content, LocalDateTime createdAt) {
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }
    
    public Comment(String id, Author author, String content, LocalDateTime createdAt) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

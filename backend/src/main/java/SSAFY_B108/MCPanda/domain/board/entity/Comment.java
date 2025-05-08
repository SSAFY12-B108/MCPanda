package SSAFY_B108.MCPanda.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 게시글의 댓글 정보를 담는 임베디드 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {
    
    private String id;
    private Author author;
    private String content;
    private LocalDateTime createdAt;
    
    @Builder
    public Comment(String id, Author author, String content) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
} 
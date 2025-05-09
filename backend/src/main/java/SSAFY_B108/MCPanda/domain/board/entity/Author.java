package SSAFY_B108.MCPanda.domain.board.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 게시글의 작성자 정보를 담는 임베디드 객체
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Author {
    
    private String id;
    private String name;
    private String profileImage;
    
    @Builder
    public Author(String id, String name, String profileImage) {
        this.id = id;
        this.name = name;
        this.profileImage = profileImage;
    }
} 
package SSAFY_B108.MCPanda.domain.board.dto;

import SSAFY_B108.MCPanda.domain.board.entity.Author;
import SSAFY_B108.MCPanda.domain.board.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequestDto {
    
    @NotBlank(message = "댓글 내용은 필수 입력 항목입니다.")
    private String content;
    
    public Comment toEntity(Author author) {
        return Comment.builder()
                .id(UUID.randomUUID().toString())
                .content(content)
                .author(author)
                .build();
    }
} 
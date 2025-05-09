package SSAFY_B108.MCPanda.domain.board.dto;

import SSAFY_B108.MCPanda.domain.board.entity.Article;
import SSAFY_B108.MCPanda.domain.board.entity.Author;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleRequestDto {
    
    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 100, message = "제목은 최대 100자까지 입력 가능합니다.")
    private String title;
    
    @NotBlank(message = "내용은 필수 입력 항목입니다.")
    private String content;
    
    @Size(max = 3, message = "MCP는 최대 3개까지 선택 가능합니다.")
    private List<String> mcps;
    
    private String category;
    
    public Article toEntity(Author author, boolean isNotice) {
        return Article.builder()
                .title(title)
                .content(content)
                .mcps(mcps)
                .category(category)
                .author(author)
                .isNotice(isNotice)
                .build();
    }
} 
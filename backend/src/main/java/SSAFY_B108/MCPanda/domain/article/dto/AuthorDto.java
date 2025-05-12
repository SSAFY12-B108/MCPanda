package SSAFY_B108.MCPanda.domain.article.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 목록 조회 시 작성자 정보 DTO")
public class AuthorDto {

    @Schema(description = "작성자 이름(닉네임)", example = "홍길동")
    private String name;

    // 기본 생성자
    public AuthorDto() {
    }

    // 모든 필드를 받는 생성자
    public AuthorDto(String name) {
        this.name = name;
    }

    // Getter와 Setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

package SSAFY_B108.MCPanda.domain.article.controller;

import SSAFY_B108.MCPanda.domain.article.dto.ArticleCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.entity.Article;
import SSAFY_B108.MCPanda.domain.article.service.ArticleService;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * 게시글(Article) 관련 API 요청을 처리하는 컨트롤러입니다.
 * 게시글의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Tag(name = "게시글 API", description = "게시글 CRUD 작업을 위한 API 명세")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 새로운 게시글을 생성합니다.
     * 요청 본문으로 게시글 제목, 내용, MCP 태그를 받으며,
     * 현재 인증된 사용자의 정보가 작성자로 자동 등록됩니다.
     *
     * @param requestDto 게시글 생성에 필요한 데이터를 담은 DTO (title, content, mcps)
     * @param loggedInMember 현재 인증된 사용자 정보 (Spring Security가 자동으로 주입)
     * @return 생성된 게시글 정보(Article)와 함께 HTTP 201 Created 상태 코드를 반환합니다.
     *         로그인하지 않은 경우 HTTP 401 Unauthorized 상태 코드와 에러 메시지를 반환합니다.
     */
    @Operation(
            summary = "게시글 생성",
            description = "새로운 게시글을 시스템에 등록합니다. 요청 본문에는 제목, 내용, MCP 태그 목록을 포함해야 합니다." +
                          " API 요청 시 헤더에 유효한 인증 토큰(Bearer Token)이 필요합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 형식 (예: 필수 필드 누락)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "요청 데이터가 유효하지 않습니다."))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요 또는 유효하지 않은 토큰)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "로그인이 필요합니다."))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "서버 처리 중 오류가 발생했습니다.")))
    })
    @PostMapping
    public ResponseEntity<?> createArticle(
            @RequestBody ArticleCreateRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal Member loggedInMember
    ) {
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String memberId = loggedInMember.getId().toString();
        String nickname = loggedInMember.getNickname();

        Article createdArticle = articleService.createArticle(requestDto, memberId, nickname);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArticle);
    }

    /**
     * 특정 ID의 게시글 상세 정보를 조회합니다.
     *
     * @param articleId 조회할 게시글의 ID (URL 경로 변수로 전달)
     * @return 해당 ID의 게시글 정보(Article)와 함께 HTTP 200 OK 상태 코드를 반환합니다.
     *         게시글을 찾을 수 없는 경우 HTTP 404 Not Found 상태 코드와 에러 메시지를 반환합니다.
     */
    @Operation(
            summary = "게시글 상세 조회",
            description = "지정된 ID를 가진 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "해당 ID의 게시글을 찾을 수 없습니다."))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "서버 처리 중 오류가 발생했습니다.")))
    })
    @GetMapping("/{articleId}")
    public ResponseEntity<?> findArticleById(
            @Parameter(description = "조회할 게시글의 고유 ID", required = true, example = "60c72b2f9b1d8c1f7c8e4f2a")
            @PathVariable String articleId
    ) {
        Optional<Article> articleOptional = articleService.findArticleById(articleId);

        if (articleOptional.isPresent()) {
            return ResponseEntity.ok(articleOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 게시글을 찾을 수 없습니다: " + articleId);
        }
    }
}

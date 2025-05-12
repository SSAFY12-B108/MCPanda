package SSAFY_B108.MCPanda.domain.article.controller;

import SSAFY_B108.MCPanda.domain.article.dto.ArticleCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticlePageResponseDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticleUpdateRequestDto;
import SSAFY_B108.MCPanda.domain.article.entity.Article;
import SSAFY_B108.MCPanda.domain.article.repository.ArticleRepository;
import SSAFY_B108.MCPanda.domain.article.repository.RecommendationRepository;
import SSAFY_B108.MCPanda.domain.article.entity.Recommendation;
import SSAFY_B108.MCPanda.domain.article.dto.ArticleRecommendResponseDto;
import SSAFY_B108.MCPanda.global.exception.ArticleNotFoundException;
import SSAFY_B108.MCPanda.global.exception.UnauthorizedOperationException;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import SSAFY_B108.MCPanda.domain.article.service.ArticleService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "게시글 API", description = "게시글 CRUD 및 추천 기능을 위한 API 명세")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(
            summary = "게시글 생성",
            description = "새로운 게시글을 시스템에 등록합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
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

    @Operation(
            summary = "게시글 상세 조회",
            description = "지정된 ID를 가진 게시글의 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Article.class))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @GetMapping("/{articleId}")
    public ResponseEntity<?> findArticleById(
            @Parameter(description = "조회할 게시글의 고유 ID", required = true)
            @PathVariable String articleId
    ) {
        Optional<Article> articleOptional = articleService.findArticleById(articleId);
        if (articleOptional.isPresent()) {
            return ResponseEntity.ok(articleOptional.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID의 게시글을 찾을 수 없습니다: " + articleId);
        }
    }

    @Operation(
            summary = "게시글 전체 목록 조회",
            description = "게시글 전체 목록을 검색, 정렬, 페이징하여 조회합니다."
    )
    @ApiResponses(value = { /* ... */ })
    @GetMapping
    public ResponseEntity<ArticlePageResponseDto> findAllArticles(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "latest") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ArticlePageResponseDto articlePageResponseDto = articleService.findAllArticles(search, type, page, size);
        return ResponseEntity.ok(articlePageResponseDto);
    }

    @Operation(
            summary = "게시글 수정",
            description = "지정된 ID를 가진 게시글의 내용을 수정합니다. 작성자 본인만 수정 가능합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 수정 성공", content = @Content(schema = @Schema(implementation = Article.class))),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PutMapping("/{articleId}")
    public ResponseEntity<?> updateArticle(
            @PathVariable String articleId,
            @RequestBody ArticleUpdateRequestDto requestDto,
            @AuthenticationPrincipal Member loggedInMember) {
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String loggedInMemberId = loggedInMember.getId().toString();
        try {
            Article updatedArticle = articleService.updateArticle(articleId, requestDto, loggedInMemberId);
            return ResponseEntity.ok(updatedArticle);
        } catch (ArticleNotFoundException e) {
            throw e; // 또는 ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            throw e; // 또는 ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(
            summary = "게시글 삭제",
            description = "지정된 ID를 가진 게시글을 시스템에서 영구적으로 삭제합니다. 작성자 본인만 삭제 가능합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"), // 또는 204 No Content
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "403", description = "접근 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @DeleteMapping("/{articleId}")
    public ResponseEntity<?> deleteArticle(
            @PathVariable String articleId,
            @AuthenticationPrincipal Member loggedInMember) {
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }
        String loggedInMemberId = loggedInMember.getId().toString();
        try {
            articleService.deleteArticle(articleId, loggedInMemberId);
            return ResponseEntity.ok().body(java.util.Map.of("message", "게시글이 성공적으로 삭제되었습니다."));
            // 또는 return ResponseEntity.noContent().build();
        } catch (ArticleNotFoundException e) {
            throw e;
        } catch (UnauthorizedOperationException e) {
            throw e;
        }
    }

    @Operation(
            summary = "게시글 추천/추천 취소",
            description = "지정된 ID의 게시글을 현재 로그인한 사용자가 추천하거나 이미 추천했다면 취소합니다." +
                          " API 요청 시 헤더에 유효한 인증 토큰(Bearer Token)이 필요합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 추천/추천 취소 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ArticleRecommendResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (로그인 필요 또는 유효하지 않은 토큰)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "로그인이 필요합니다."))),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "해당 ID의 게시글을 찾을 수 없습니다."))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type="string", example = "서버 처리 중 오류가 발생했습니다.")))
    })
    @PostMapping("/{articleId}/recommends")
    public ResponseEntity<?> recommendArticle(
            @Parameter(description = "추천/추천 취소할 게시글의 고유 ID", required = true, example = "60c72b2f9b1d8c1f7c8e4f2a")
            @PathVariable String articleId,
            @Parameter(hidden = true) @AuthenticationPrincipal Member loggedInMember
    ) {
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String memberId = loggedInMember.getId().toString();

        try {
            ArticleRecommendResponseDto responseDto = articleService.recommendOrUnrecommendArticle(articleId, memberId);
            return ResponseEntity.ok(responseDto);
        } catch (ArticleNotFoundException e) {
            throw e;
        }
    }
}

package SSAFY_B108.MCPanda.domain.board.controller;

import SSAFY_B108.MCPanda.domain.board.dto.ArticleListResponseDto;
import SSAFY_B108.MCPanda.domain.board.dto.ArticleRequestDto;
import SSAFY_B108.MCPanda.domain.board.dto.ArticleResponseDto;
import SSAFY_B108.MCPanda.domain.board.dto.CommentRequestDto;
import SSAFY_B108.MCPanda.domain.board.service.ArticleService;
import SSAFY_B108.MCPanda.global.response.ApiResult;
import SSAFY_B108.MCPanda.global.response.StatusCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
@Tag(name = "게시글 API", description = "게시글 CRUD 및 댓글 관련 API")
public class ArticleController {

    private final ArticleService articleService;
    
    // TODO: Security 구현 시 Principal에서 사용자 정보 추출
    // 임시로 사용할 사용자 정보
    private final String TEMP_USER_ID = "user123";
    private final String TEMP_USER_NAME = "홍길동";
    private final String TEMP_PROFILE_IMAGE = "https://example.com/profile.jpg";
    
    /**
     * 게시글 목록 조회 (검색, 정렬, 페이징)
     */
    @Operation(summary = "게시글 목록 조회", description = "검색어, 정렬 타입, 페이지 번호를 기반으로 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ApiResult<ArticleListResponseDto> getArticles(
            @Parameter(description = "검색어") @RequestParam(required = false) String search,
            @Parameter(description = "정렬 타입 (latest: 최신순, recommend: 추천순)") @RequestParam(defaultValue = "latest") String type,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page) {
        
        ArticleListResponseDto response = articleService.findArticles(search, type, page);
        return ApiResult.success(response);
    }
    
    /**
     * 카테고리별 게시글 목록 조회
     */
    @Operation(summary = "카테고리별 게시글 목록 조회", description = "특정 카테고리의 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리별 게시글 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{category_pk}")
    public ApiResult<ArticleListResponseDto> getArticlesByCategory(
            @Parameter(description = "카테고리 식별자") @PathVariable("category_pk") String category,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page) {
        
        ArticleListResponseDto response = articleService.findArticlesByCategory(category, page);
        return ApiResult.success(response);
    }
    
    /**
     * 게시글 상세 조회
     */
    @Operation(summary = "게시글 상세 조회", description = "게시글 ID로 게시글 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{article_pk}")
    public ApiResult<ArticleResponseDto> getArticle(
            @Parameter(description = "게시글 ID") @PathVariable("article_pk") String id) {
        ArticleResponseDto response = articleService.findArticle(id);
        return ApiResult.success(response);
    }
    
    /**
     * 게시글 생성
     */
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 작성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ApiResult<ArticleResponseDto> createArticle(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시글 작성 정보", required = true)
            @Valid @RequestBody ArticleRequestDto requestDto) {
        // TODO: 인증된 사용자 정보 활용
        ArticleResponseDto response = articleService.createArticle(
                requestDto, TEMP_USER_ID, TEMP_USER_NAME, TEMP_PROFILE_IMAGE);
        
        return ApiResult.of(StatusCode.CREATED, response);
    }
    
    /**
     * 게시글 수정
     */
    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "수정 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PutMapping("/{article_pk}")
    public ApiResult<ArticleResponseDto> updateArticle(
            @Parameter(description = "게시글 ID") @PathVariable("article_pk") String id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "게시글 수정 정보", required = true)
            @Valid @RequestBody ArticleRequestDto requestDto) {
        
        // TODO: 인증된 사용자 정보 활용
        ArticleResponseDto response = articleService.updateArticle(id, requestDto, TEMP_USER_ID);
        
        return ApiResult.success(response);
    }
    
    /**
     * 게시글 삭제
     */
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{article_pk}")
    public ApiResult<Void> deleteArticle(
            @Parameter(description = "게시글 ID") @PathVariable("article_pk") String id) {
        // TODO: 인증된 사용자 정보 활용
        articleService.deleteArticle(id, TEMP_USER_ID);
        
        return ApiResult.success("게시글이 성공적으로 삭제되었습니다.", null);
    }
    
    /**
     * 게시글 추천
     */
    @Operation(summary = "게시글 추천", description = "게시글에 추천을 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 추천 성공"),
        @ApiResponse(responseCode = "400", description = "자신의 게시글 추천 불가"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{article_pk}/recommends")
    public ApiResult<Void> recommendArticle(
            @Parameter(description = "게시글 ID") @PathVariable("article_pk") String id) {
        // TODO: 인증된 사용자 정보 활용
        articleService.recommendArticle(id, TEMP_USER_ID);
        
        return ApiResult.success("게시글을 추천하였습니다.", null);
    }
    
    /**
     * 댓글 작성
     */
    @Operation(summary = "댓글 작성", description = "게시글에 새로운 댓글을 작성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{article_pk}/comment")
    public ApiResult<ArticleResponseDto> addComment(
            @Parameter(description = "게시글 ID") @PathVariable("article_pk") String articleId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "댓글 작성 정보", required = true)
            @Valid @RequestBody CommentRequestDto requestDto) {
        
        // TODO: 인증된 사용자 정보 활용
        ArticleResponseDto response = articleService.addComment(
                articleId, requestDto, TEMP_USER_ID, TEMP_USER_NAME, TEMP_PROFILE_IMAGE);
        
        return ApiResult.success(response);
    }
    
    /**
     * 댓글 삭제
     */
    @Operation(summary = "댓글 삭제", description = "게시글의 댓글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
        @ApiResponse(responseCode = "404", description = "게시글 또는 댓글을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{article_pk}/{comment_pk}")
    public ApiResult<ArticleResponseDto> deleteComment(
            @Parameter(description = "게시글 ID") @PathVariable("article_pk") String articleId,
            @Parameter(description = "댓글 ID") @PathVariable("comment_pk") String commentId) {
        
        // TODO: 인증된 사용자 정보 활용
        ArticleResponseDto response = articleService.deleteComment(articleId, commentId, TEMP_USER_ID);
        
        return ApiResult.success(response);
    }
} 
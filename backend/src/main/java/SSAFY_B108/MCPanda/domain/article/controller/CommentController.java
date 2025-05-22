package SSAFY_B108.MCPanda.domain.article.controller;

import SSAFY_B108.MCPanda.domain.article.dto.CommentRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.CommentResponseDto;
import SSAFY_B108.MCPanda.domain.article.service.CommentService;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.global.exception.ArticleNotFoundException;
import SSAFY_B108.MCPanda.global.exception.CommentNotFoundException;
import SSAFY_B108.MCPanda.global.exception.UnauthorizedOperationException;
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

import java.util.Map;

@Tag(name = "댓글 API", description = "댓글 작성 및 삭제를 위한 API 명세")
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(
            summary = "댓글 작성",
            description = "게시글에 새로운 댓글을 작성합니다. 로그인이 필요한 기능입니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CommentResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    @PostMapping("/{article_pk}/comment")
    public ResponseEntity<?> createComment(
            @Parameter(description = "댓글을 작성할 게시글의 ID", required = true)
            @PathVariable("article_pk") String articleId,
            @RequestBody CommentRequestDto requestDto,
            @Parameter(hidden = true) @AuthenticationPrincipal Member loggedInMember
    ) {
        // 로그인 확인
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 댓글 작성 서비스 호출
            String memberId = loggedInMember.getId().toString();
            String nickname = loggedInMember.getNickname();
            CommentResponseDto responseDto = commentService.createComment(articleId, requestDto, memberId, nickname);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (ArticleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Operation(
            summary = "댓글 삭제",
            description = "지정된 게시글의 특정 댓글을 삭제합니다. 로그인이 필요하며 본인이 작성한 댓글만 삭제할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 또는 댓글을 찾을 수 없음")
    })
    @DeleteMapping("/{article_pk}/{comment_pk}")
    public ResponseEntity<?> deleteComment(
            @Parameter(description = "댓글이 작성된 게시글의 ID", required = true)
            @PathVariable("article_pk") String articleId,
            @Parameter(description = "삭제할 댓글의 ID", required = true)
            @PathVariable("comment_pk") String commentId,
            @Parameter(hidden = true) @AuthenticationPrincipal Member loggedInMember
    ) {
        // 로그인 확인
        if (loggedInMember == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            // 댓글 삭제 서비스 호출
            String memberId = loggedInMember.getId().toString();
            commentService.deleteComment(articleId, commentId, memberId);
            
            // 204 No Content 또는 메시지 포함 응답
            return ResponseEntity.ok().body(Map.of("message", "댓글이 삭제되었습니다."));
            // return ResponseEntity.noContent().build(); // 204 No Content 응답
        } catch (ArticleNotFoundException | CommentNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (UnauthorizedOperationException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
} 
package SSAFY_B108.MCPanda.domain.article.service;

import SSAFY_B108.MCPanda.domain.article.dto.CommentRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.CommentResponseDto;
import SSAFY_B108.MCPanda.domain.article.entity.Article;
import SSAFY_B108.MCPanda.domain.article.entity.Author;
import SSAFY_B108.MCPanda.domain.article.entity.Comment;
import SSAFY_B108.MCPanda.domain.article.repository.ArticleRepository;
import SSAFY_B108.MCPanda.global.exception.ArticleNotFoundException;
import SSAFY_B108.MCPanda.global.exception.CommentNotFoundException;
import SSAFY_B108.MCPanda.global.exception.UnauthorizedOperationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CommentService {

    private final ArticleRepository articleRepository;

    @Autowired
    public CommentService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 댓글을 작성합니다.
     *
     * @param articleId 댓글을 작성할 게시글 ID
     * @param requestDto 댓글 작성 요청 DTO
     * @param memberId 작성자 회원 ID
     * @param nickname 작성자 닉네임
     * @return 댓글 작성 결과 응답 DTO
     * @throws ArticleNotFoundException 게시글을 찾을 수 없는 경우
     */
    @Transactional
    public CommentResponseDto createComment(String articleId, CommentRequestDto requestDto, String memberId, String nickname) {
        // 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("댓글을 작성하려는 게시글을 찾을 수 없습니다: " + articleId));

        // 댓글 생성
        Author author = new Author(memberId, nickname);
        Comment comment = new Comment(
                UUID.randomUUID().toString(),
                author,
                requestDto.getContent(),
                LocalDateTime.now()
        );

        // 게시글의 댓글 목록에 추가
        if (article.getComments() == null) {
            article.setComments(new ArrayList<>());
        }
        article.getComments().add(comment);

        // 게시글 저장
        articleRepository.save(article);

        // 응답 반환
        return new CommentResponseDto(comment.getId(), "댓글이 성공적으로 작성되었습니다.");
    }

    /**
     * 댓글을 삭제합니다.
     *
     * @param articleId 댓글이 작성된 게시글 ID
     * @param commentId 삭제할 댓글 ID
     * @param loggedInMemberId 현재 로그인한 사용자의 ID
     * @throws ArticleNotFoundException 게시글을 찾을 수 없는 경우
     * @throws CommentNotFoundException 댓글을 찾을 수 없는 경우
     * @throws UnauthorizedOperationException 삭제 권한이 없는 경우 (본인 댓글이 아닌 경우)
     */
    @Transactional
    public void deleteComment(String articleId, String commentId, String loggedInMemberId) {
        // 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("댓글을 삭제하려는 게시글을 찾을 수 없습니다: " + articleId));

        // 게시글의 댓글 목록에서 해당 ID를 가진 댓글 찾기
        List<Comment> comments = article.getComments();
        if (comments == null || comments.isEmpty()) {
            throw new CommentNotFoundException("해당 게시글에 댓글이 없습니다.");
        }

        // 댓글 찾기
        Comment commentToDelete = comments.stream()
                .filter(c -> c.getId().equals(commentId))
                .findFirst()
                .orElseThrow(() -> new CommentNotFoundException("해당 ID의 댓글을 찾을 수 없습니다: " + commentId));

        // 댓글 작성자 확인
        if (commentToDelete.getAuthor() == null || 
            commentToDelete.getAuthor().getMemberId() == null || 
            !commentToDelete.getAuthor().getMemberId().equals(loggedInMemberId)) {
            throw new UnauthorizedOperationException("해당 댓글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제
        comments.remove(commentToDelete);

        // 게시글 저장
        articleRepository.save(article);
    }
} 
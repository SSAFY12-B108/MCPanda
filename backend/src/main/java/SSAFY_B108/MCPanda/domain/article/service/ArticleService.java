package SSAFY_B108.MCPanda.domain.article.service;

import SSAFY_B108.MCPanda.domain.article.dto.ArticleCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.entity.Article;
import SSAFY_B108.MCPanda.domain.article.entity.Author;
import SSAFY_B108.MCPanda.domain.article.repository.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 게시글을 생성합니다.
     *
     * @param requestDto 게시글 생성 요청 데이터 (title, content, mcps)
     * @param memberId 작성자 회원 ID
     * @param nickname 작성자 닉네임
     * @return 생성된 Article 엔티티
     */
    public Article createArticle(ArticleCreateRequestDto requestDto, String memberId, String nickname) {
        // 1. 작성자(Author) 객체 생성
        Author author = new Author(memberId, nickname);

        // 2. Article 엔티티 객체 생성 및 값 설정
        Article newArticle = new Article();
        newArticle.setTitle(requestDto.getTitle());
        newArticle.setContent(requestDto.getContent());
        newArticle.setMcps(requestDto.getMcps());

        newArticle.setAuthor(author); // 작성자 정보 설정
        newArticle.setCreatedAt(LocalDateTime.now()); // 현재 시간으로 생성일시 설정
        newArticle.setNotice(false); // 기본값: 공지 아님
        newArticle.setRecommendCount(0); // 기본값: 추천수 0
        newArticle.setComments(new ArrayList<>()); // 기본값: 빈 댓글 목록

        // 3. Repository를 통해 데이터베이스에 저장
        return articleRepository.save(newArticle);
    }

    /**
     * ID로 특정 게시글을 조회합니다.
     * 조회수 증가는 여기서는 일단 고려하지 않습니다. (필요하다면 추가 로직 구현)
     *
     * @param articleId 조회할 게시글의 ID
     * @return 찾아낸 Article 객체. 해당 ID의 게시글이 없으면 Optional.empty() 반환.
     *         (또는 예외를 발생시킬 수도 있습니다. 지금은 Optional을 반환합니다.)
     */
    @Transactional(readOnly = true)
    public Optional<Article> findArticleById(String articleId) {
        return articleRepository.findById(articleId);
    }

    // 여기에 앞으로 게시글 수정, 삭제, 목록 조회 등의 메소드가 추가될 예정입니다.
}

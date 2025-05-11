package SSAFY_B108.MCPanda.domain.article.service;

import SSAFY_B108.MCPanda.domain.article.dto.ArticleCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticleListInfoResponseDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticlePageResponseDto;
import SSAFY_B108.MCPanda.domain.article.dto.AuthorDto;
import SSAFY_B108.MCPanda.domain.article.entity.Article;
import SSAFY_B108.MCPanda.domain.article.entity.Author;
import SSAFY_B108.MCPanda.domain.article.repository.ArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

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

    /**
     * 게시글 전체 목록을 조회합니다. (검색, 정렬, 페이징 기능 포함)
     *
     * @param search 검색어 (제목 또는 내용 대상)
     * @param type 정렬 타입 ("latest" 또는 "recommend")
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 당 게시글 수
     * @return 페이징 처리된 게시글 목록 응답 DTO
     */
    @Transactional(readOnly = true)
    public ArticlePageResponseDto findAllArticles(String search, String type, int page, int size) {
        // 1. 정렬 조건 설정
        Sort sort = Sort.by("createdAt").descending(); // 기본값: 최신순
        if ("recommend".equalsIgnoreCase(type)) {
            sort = Sort.by("recommendCount").descending().and(Sort.by("createdAt").descending());
        }

        // 2. Pageable 객체 생성 (페이지 번호는 0부터 시작하므로 page-1)
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : 0, size, sort);

        // 3. 검색 조건에 따른 데이터 조회
        Page<Article> articlePage;
        if (search != null && !search.trim().isEmpty()) {
            // TODO: ArticleRepository에 검색을 위한 메소드 추가 필요
            // 예: articleRepository.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(search, search, pageable);
            // 지금은 임시로 전체 조회 후 필터링하는 방식으로 구현 (비효율적, 추후 Repository 수정 필요)
            // 여기서는 우선 모든 게시글을 가져온 후 서비스단에서 필터링하는 간단한 예시를 보여드리지만,
            // 실제로는 데이터베이스 레벨에서 검색하는 것이 훨씬 효율적입니다.
            // 우선은 검색 기능 없이 페이징만 구현된 형태로 진행하고, 검색 기능은 Repository 수정과 함께 다시 다루겠습니다.
            articlePage = articleRepository.findAll(pageable);
        } else {
            articlePage = articleRepository.findAll(pageable);
        }

        // 4. 조회된 Article 엔티티들을 ArticleListInfoResponseDto로 변환
        List<ArticleListInfoResponseDto> articleDtos = articlePage.getContent().stream()
                .map(this::convertToArticleListInfoResponseDto)
                .collect(Collectors.toList());

        // 5. 최종 응답 DTO 생성
        return new ArticlePageResponseDto(
                articlePage.getNumber() + 1, // 클라이언트에게는 페이지 번호를 1부터 시작하는 것으로 전달
                articlePage.getTotalPages(),
                articlePage.getTotalElements(),
                articleDtos
        );
    }

    // Article 엔티티를 ArticleListInfoResponseDto로 변환하는 헬퍼 메소드
    private ArticleListInfoResponseDto convertToArticleListInfoResponseDto(Article article) {
        AuthorDto authorDto = null;
        if (article.getAuthor() != null && article.getAuthor().getNickname() != null) {
            authorDto = new AuthorDto(article.getAuthor().getNickname());
        }

        String formattedCreatedAt = null;
        if (article.getCreatedAt() != null) {
            formattedCreatedAt = article.getCreatedAt().format(LOCAL_DATE_TIME_FORMATTER);
        }
        
        int commentsCount = 0;
        if (article.getComments() != null) {
            commentsCount = article.getComments().size();
        }

        return new ArticleListInfoResponseDto(
                article.getId(),
                article.isNotice(),
                article.getTitle(),
                truncateContent(article.getContent(), 100), // 내용 요약 (예: 100자)
                article.getMcps(),
                formattedCreatedAt,
                authorDto,
                article.getRecommendCount(),
                commentsCount
        );
    }

    // 내용 요약을 위한 헬퍼 메소드 (간단한 버전)
    private String truncateContent(String content, int maxLength) {
        if (content == null || content.length() <= maxLength) {
            return content;
        }
        return content.substring(0, maxLength) + "...";
    }

    // 여기에 앞으로 게시글 수정, 삭제, 목록 조회 등의 메소드가 추가될 예정입니다.
}

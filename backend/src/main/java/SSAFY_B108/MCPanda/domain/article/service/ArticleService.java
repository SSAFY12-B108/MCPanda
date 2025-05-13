package SSAFY_B108.MCPanda.domain.article.service;

import SSAFY_B108.MCPanda.domain.article.dto.ArticleCreateRequestDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticleListInfoResponseDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticlePageResponseDto;
import SSAFY_B108.MCPanda.domain.article.dto.AuthorDto;
import SSAFY_B108.MCPanda.domain.article.dto.ArticleUpdateRequestDto;
import SSAFY_B108.MCPanda.domain.article.entity.Article;
import SSAFY_B108.MCPanda.domain.article.entity.Author;
import SSAFY_B108.MCPanda.domain.article.repository.ArticleRepository;
import SSAFY_B108.MCPanda.domain.article.repository.RecommendationRepository;
import SSAFY_B108.MCPanda.domain.article.entity.Recommendation;
import SSAFY_B108.MCPanda.domain.article.dto.ArticleRecommendResponseDto;
import SSAFY_B108.MCPanda.global.exception.ArticleNotFoundException;
import SSAFY_B108.MCPanda.global.exception.UnauthorizedOperationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import SSAFY_B108.MCPanda.domain.article.repository.MCPRepository;
import SSAFY_B108.MCPanda.domain.article.entity.MCP;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

@Service
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final RecommendationRepository recommendationRepository;
    private final MCPRepository mcpRepository;
    private static final DateTimeFormatter ISO_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Autowired
    public ArticleService(ArticleRepository articleRepository,
                          RecommendationRepository recommendationRepository,
                          MCPRepository mcpRepository) {
        this.articleRepository = articleRepository;
        this.recommendationRepository = recommendationRepository;
        this.mcpRepository = mcpRepository;
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
        
        // MCP 이름만 저장하고 실제 내용은 조회 시 DB에서 가져옴
        Map<String, Object> mcpNames = new HashMap<>();
        if (requestDto.getMcps() != null) {
            for (String key : requestDto.getMcps().keySet()) {
                mcpNames.put(key, true);
            }
        }
        newArticle.setMcps(mcpNames);

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
     *
     * @param articleId 조회할 게시글의 ID
     * @return 찾아낸 Article 객체. 해당 ID의 게시글이 없으면 Optional.empty() 반환.
     */
    @Transactional(readOnly = true)
    public Optional<Article> findArticleById(String articleId) {
        Optional<Article> articleOpt = articleRepository.findById(articleId);
        
        if (articleOpt.isPresent()) {
            Article article = articleOpt.get();
            
            // MCP 정보를 DB에서 로드하여 설정
            Map<String, Object> mcpsMap = article.getMcps();
            if (mcpsMap != null && !mcpsMap.isEmpty()) {
                Map<String, Object> enrichedMcps = new HashMap<>();
                
                // mcps 맵의 각 키(MCP 이름)에 대해 DB에서 MCP 정보 조회
                for (String mcpName : mcpsMap.keySet()) {
                    Optional<MCP> mcpOpt = mcpRepository.findByName(mcpName);
                    if (mcpOpt.isPresent()) {
                        MCP mcp = mcpOpt.get();
                        enrichedMcps.put(mcpName, mcp.getMcpServers());
                    }
                }
                
                // 기존 mcps를 DB에서 조회한 정보로 대체
                article.setMcps(enrichedMcps);
            }
            
            return Optional.of(article);
        }
        
        return articleOpt;
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
            articlePage = articleRepository.findAll(pageable); // 현재는 검색 미구현
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

    /**
     * 게시글을 수정합니다.
     *
     * @param articleId 수정할 게시글의 ID
     * @param requestDto 수정할 내용을 담은 DTO (title, content, mcps)
     * @param loggedInMemberId 현재 로그인한 사용자의 ID (Member 엔티티의 ID를 String으로 변환한 값)
     * @return 수정된 Article 엔티티
     * @throws ArticleNotFoundException 게시글을 찾을 수 없는 경우
     * @throws UnauthorizedOperationException 수정 권한이 없는 경우 (본인 게시글이 아닌 경우)
     */
    @Transactional
    public Article updateArticle(String articleId, ArticleUpdateRequestDto requestDto, String loggedInMemberId) {
        // 1. ID를 사용해 기존 게시글을 조회합니다. 게시글이 없으면 ArticleNotFoundException 발생.
        Article existingArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("해당 ID의 게시글을 찾을 수 없습니다: " + articleId));

        // 2. 현재 로그인한 사용자가 게시글 작성자인지 확인합니다.
        if (existingArticle.getAuthor() == null || 
            existingArticle.getAuthor().getMemberId() == null || 
            !existingArticle.getAuthor().getMemberId().equals(loggedInMemberId)) {
            throw new UnauthorizedOperationException("해당 게시글을 수정할 권한이 없습니다.");
        }

        // 3. DTO로부터 받은 정보로 게시글 내용을 업데이트합니다.
        boolean needsUpdate = false;
        if (requestDto.getTitle() != null && !requestDto.getTitle().equals(existingArticle.getTitle())) {
            existingArticle.setTitle(requestDto.getTitle());
            needsUpdate = true;
        }
        if (requestDto.getContent() != null && !requestDto.getContent().equals(existingArticle.getContent())) {
            existingArticle.setContent(requestDto.getContent());
            needsUpdate = true;
        }

        // 4. 업데이트된 게시글을 저장합니다.
        return articleRepository.save(existingArticle);
    }

    /**
     * 게시글을 삭제합니다. (Hard Delete)
     *
     * @param articleId 삭제할 게시글의 ID
     * @param loggedInMemberId 현재 로그인한 사용자의 ID (Member 엔티티의 ID를 String으로 변환한 값)
     * @throws ArticleNotFoundException 게시글을 찾을 수 없는 경우
     * @throws UnauthorizedOperationException 삭제 권한이 없는 경우 (본인 게시글이 아닌 경우)
     */
    @Transactional // 데이터 변경이 있으므로 @Transactional 어노테이션 추가
    public void deleteArticle(String articleId, String loggedInMemberId) {
        // 1. ID를 사용해 기존 게시글을 조회합니다. 게시글이 없으면 ArticleNotFoundException 발생.
        Article articleToDelete = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("삭제하려는 게시글을 찾을 수 없습니다: " + articleId));

        // 2. 현재 로그인한 사용자가 게시글 작성자인지 확인합니다.
        if (articleToDelete.getAuthor() == null ||
            articleToDelete.getAuthor().getMemberId() == null ||
            !articleToDelete.getAuthor().getMemberId().equals(loggedInMemberId)) {
            throw new UnauthorizedOperationException("해당 게시글을 삭제할 권한이 없습니다.");
        }

        // 3. 게시글을 삭제합니다.
        // MongoRepository의 deleteById 메소드는 내부적으로 해당 ID의 문서를 찾아 삭제합니다.
        // delete(articleToDelete)를 사용해도 동일하게 동작합니다.
        articleRepository.deleteById(articleId);
        // 또는 articleRepository.delete(articleToDelete);
    }

    /**
     * 게시글을 추천하거나 추천을 취소합니다 (토글 방식).
     *
     * @param articleId 대상 게시글의 ID
     * @param memberId  현재 로그인한 사용자의 ID
     * @return 추천 처리 결과 (메시지, 업데이트된 추천 수, 현재 추천 상태)
     * @throws ArticleNotFoundException 게시글을 찾을 수 없는 경우
     */
    @Transactional
    public ArticleRecommendResponseDto recommendOrUnrecommendArticle(String articleId, String memberId) {
        // 1. 게시글 조회
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new ArticleNotFoundException("추천하려는 게시글을 찾을 수 없습니다: " + articleId));

        // 2. 현재 사용자가 해당 게시글을 이미 추천했는지 확인
        Optional<Recommendation> existingRecommendation = recommendationRepository.findByMemberIdAndArticleId(memberId, articleId);

        String message;
        boolean isLiked;

        if (existingRecommendation.isPresent()) {
            // 3-1. 이미 추천한 경우: 추천 취소 (Recommendation 레코드 삭제)
            recommendationRepository.delete(existingRecommendation.get());
            article.setRecommendCount(Math.max(0, article.getRecommendCount() - 1)); // 추천수 1 감소 (음수 방지)
            message = "추천 취소됨";
            isLiked = false;
        } else {
            // 3-2. 아직 추천하지 않은 경우: 추천 (Recommendation 레코드 생성)
            Recommendation newRecommendation = new Recommendation(memberId, articleId);
            // @CreatedDate 어노테이션을 Recommendation 엔티티에 사용했다면 createdAt은 자동으로 설정됩니다.
            // 만약 Auditing 미사용 시 또는 수동 설정 원할 시:
            // newRecommendation.setCreatedAt(LocalDateTime.now());
            recommendationRepository.save(newRecommendation);
            article.setRecommendCount(article.getRecommendCount() + 1); // 추천수 1 증가
            message = "추천 완료";
            isLiked = true;
        }

        // 4. 변경된 Article 정보 저장 (recommendCount 업데이트)
        articleRepository.save(article);

        // 5. 응답 DTO 생성 및 반환
        return new ArticleRecommendResponseDto(message, article.getRecommendCount(), isLiked);
    }

    // 여기에 앞으로 게시글 삭제 등의 메소드가 추가될 예정입니다.
}

package SSAFY_B108.MCPanda.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.CustomOAuth2User;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.domain.member.repository.MemberRepository;
import org.bson.types.ObjectId;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    // JWT 서명에 사용할 키
    private final Key key;

    // access Token 유효기간 (밀리초 단위)
    private final long accessTokenValidityInMilliseconds;

    // refresh Token 유효기간 (밀리초 단위)
    private final long refreshTokenValidityInMilliseconds;

    private final MemberRepository memberRepository;

    /**
     * 설정 값을 주입받아 초기화하는 생성자
     *
     * @param secretKey JWT 서명에 사용할 비밀 키
     * @param accessTokenValidityInSeconds AccessToken 유효 시간 (초 단위)
     * @param refreshTokenValidityInSeconds RefreshToken 유효 시간 (초 단위)
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds,
            MemberRepository memberRepository) {

        log.info("Secret key: {}", secretKey);
        // 비밀 키를 바이트 배열로 변환하여 HMAC SHA 키 생성
        try {
            // Base64 디코딩을 사용하여 키 생성
            byte[] decodedKey = Base64.getDecoder().decode(secretKey);
            log.info("Decoded key: {}", decodedKey);
            log.info("Decoded key length: {} bytes", decodedKey.length);

            this.key = Keys.hmacShaKeyFor(decodedKey);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 encoded secret key: {}", e.getMessage());
            throw new RuntimeException("JWT 시크릿 키가 유효하지 않습니다.", e);
        }

        // 초 단위를 밀리초 단위로 변환
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
        this.memberRepository = memberRepository;
    }

    /**
     * AccessToken 생성
     *
     * @param authentication 인증 정보
     * @return 생성된 JWT 토큰 문자열
     */
    public String createAccessToken(Authentication authentication) {
        // Principal 객체에서 회원 ID 추출
        String memberId = null;
        String email = null;
        String name = null;

        Object principal = authentication.getPrincipal();
        log.info("Authentication 클래스: {}", authentication.getClass().getName());
        log.info("Principal 클래스: {}", principal.getClass().getName());

        // 두 가지 Principal 타입 처리
        if (principal instanceof CustomOAuth2User) {
            // OAuth2 로그인 시
            CustomOAuth2User user = (CustomOAuth2User) principal;
            memberId = user.getMemberId(); // attributes 맵에서 "memberId" 키 값 가져옴
            email = user.getEmail();
            name = user.getName();

            log.info("CustomOAuth2User로부터 정보 추출 - memberId: {}, email: {}, name: {}", memberId, email, name);

            // memberId가 없으면 email을 기반으로 회원 조회
            if (memberId == null || memberId.isEmpty()) {
                try {
                    Member member = memberRepository.findByEmail(email).orElse(null);
                    if (member != null) {
                        memberId = member.getId().toString();
                        log.info("CustomOAuth2User - email로 회원 조회 성공, memberId: {}", memberId);
                    }
                } catch (Exception e) {
                    log.error("CustomOAuth2User - email로 회원 조회 중 오류: {}", e.getMessage());
                }
            }
        } else if (principal instanceof User) {
            // 토큰 재발급 시
            User user = (User) principal;
            email = user.getUsername(); // User의 username은 일반적으로 email

            log.info("User 타입으로부터 정보 추출 - email: {}", email);

            // email을 기반으로 MongoDB에서 Member 조회
            try {
                Member member = memberRepository.findByEmail(email).orElse(null);
                if (member != null) {
                    memberId = member.getId().toString();
                    name = member.getName(); // 회원의 이름 가져오기 (필요한 경우)
                    log.info("User 타입 - email로 회원 조회 성공, memberId: {}", memberId);
                } else {
                    log.warn("email: {}에 해당하는 회원을 찾을 수 없습니다.", email);
                }
            } catch (Exception e) {
                log.error("회원 조회 중 오류: {}", e.getMessage());
            }
        } else {
            // 기타 타입의 경우
            log.warn("지원하지 않는 Principal 타입: {}", principal != null ? principal.getClass().getName() : "null");
            email = authentication.getName();
        }

        // memberId가 추출되지 않은 경우, email을 subject로 사용
        if (memberId == null || memberId.isEmpty()) {
            log.warn("memberId 추출 실패, email을 subject로 사용: {}", email);
            memberId = email;
        }

        // 권한 정보 문자열로 변환
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(memberId)         // memberId를 subject로 설정
                .claim("auth", authorities)   // 권한 정보
                .claim("email", email)        // email 저장
                .claim("name", name)          // 이름 저장 (null 가능)
                .setIssuedAt(new Date(now))   // 발행 시간
                .setExpiration(validity)      // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512)  // 서명 알고리즘
                .compact();
    }

    /**
     * RefreshToken 생성 (권한 정보 미포함)
     *
     * @param authentication 인증 정보
     * @return 생성된 Refresh 토큰 문자열
     */
    public String createRefreshToken(Authentication authentication) {
        // 현재 시간과 유효 기간 설정
        long now = (new Date()).getTime();
        Date validity = new Date(now + this.refreshTokenValidityInMilliseconds);

        // Principal 객체에서 회원 ID 추출
        String memberId = null;
        String email;
        String name = null;

        Object principal = authentication.getPrincipal();

        // 두 가지 Principal 타입 처리
        if (principal instanceof CustomOAuth2User user) {
            memberId = user.getMemberId();
            email = user.getEmail();
            name = user.getName();

            log.info("RefreshToken - CustomOAuth2User로부터 정보 추출 - memberId: {}, email: {}", memberId, email);

            if (memberId == null || memberId.isEmpty()) {
                try {
                    Member member = memberRepository.findByEmail(email).orElse(null);
                    if (member != null) {
                        memberId = member.getId().toString();
                        log.info("RefreshToken - CustomOAuth2User, email로 회원 조회 성공, memberId: {}", memberId);
                    }
                } catch (Exception e) {
                    log.error("RefreshToken - CustomOAuth2User, email로 회원 조회 중 오류: {}", e.getMessage());
                }
            }
        } else if (principal instanceof User user) {
            email = user.getUsername();

            log.info("RefreshToken - User 타입으로부터 정보 추출 - email: {}", email);

            try {
                Member member = memberRepository.findByEmail(email).orElse(null);
                if (member != null) {
                    memberId = member.getId().toString();
                    name = member.getName();
                    log.info("RefreshToken - User 타입, email로 회원 조회 성공, memberId: {}", memberId);
                } else {
                    log.warn("RefreshToken - email: {}에 해당하는 회원을 찾을 수 없습니다.", email);
                }
            } catch (Exception e) {
                log.error("RefreshToken - 회원 조회 중 오류: {}", e.getMessage());
            }
        } else {
            email = authentication.getName();
            log.warn("RefreshToken - 지원하지 않는 Principal 타입: {}", principal != null ? principal.getClass().getName() : "null");
        }

        // memberId가 추출되지 않은 경우, email을 subject로 사용
        if (memberId == null || memberId.isEmpty()) {
            log.warn("RefreshToken - memberId 추출 실패, email을 subject로 사용: {}", email);
            memberId = email;
        }

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(memberId)          // memberId를 subject로 설정
                .claim("email", email)         // email 저장
                .claim("name", name)           // 이름 저장 (null 가능)
                .setIssuedAt(new Date(now))    // 발행 시간
                .setExpiration(validity)       // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512)  // 서명 알고리즘
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보 추출
     * @param token JWT 토큰
     * @return Spring Security Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String memberId = claims.getSubject();
            log.info("claims.getSubject()로 가져온 memberId: {}", memberId);

            // email 정보 추출
            String email = claims.get("email", String.class);
            log.info("claims에서 추출한 email: {}", email);

            // auth 클레임 처리
            String authClaim = claims.get("auth", String.class);
            Collection<? extends GrantedAuthority> authorities;

            if (authClaim != null && !authClaim.isEmpty()) {
                authorities = Arrays.stream(authClaim.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList();
            } else {
                authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            }

            // 사용자 ID가 있으면 Member 객체 조회
            if (memberId != null && !memberId.isEmpty() && ObjectId.isValid(memberId)) {
                try {
                    ObjectId objectId = new ObjectId(memberId);
                    Member member = memberRepository.findById(objectId).orElse(null);

                    if (member != null) {
                        return new UsernamePasswordAuthenticationToken(member, token, authorities);
                    }
                } catch (Exception e) {
                    log.error("Member ID 변환 오류: {}", e.getMessage());
                }
            }

            // email로 Member 조회 시도
            if (email != null && !email.isEmpty()) {
                try {
                    Member member = memberRepository.findByEmail(email).orElse(null);

                    if (member != null) {
                        return new UsernamePasswordAuthenticationToken(member, token, authorities);
                    }
                } catch (Exception e) {
                    log.error("email로 회원 조회 중 오류: {}", e.getMessage());
                }
            }

            // 유효한 ID 결정
            String userIdentifier = (email != null && !email.isEmpty()) ? email :
                    (memberId != null && !memberId.isEmpty()) ? memberId : "anonymousUser";

            return new UsernamePasswordAuthenticationToken(
                    new User(userIdentifier, "", authorities), token, authorities);

        } catch (JwtException e) {
            log.error("JWT 토큰 파싱 오류: {}", e.getMessage());
            return new AnonymousAuthenticationToken(
                    "anonymous", "anonymousUser",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS")));
        }
    }

    /**
     * 토큰 유효성 검증
     * @param token 검증할 JWT 토큰
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            // 토큰 파싱 시도
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰 파싱 실패 시 로그 기록
            log.info("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 사용자 이름(ID) 추출
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getMemberIdFromToken(String token) {
        try {
            // 토큰에서 memberId 추출
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            // 만료된 토큰에서도 사용자 이름 추출 가능
            return e.getClaims().getSubject();
        }
    }
}
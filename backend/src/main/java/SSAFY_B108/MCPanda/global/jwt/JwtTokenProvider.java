package SSAFY_B108.MCPanda.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
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
        // 1. Principal 객체에서 회원 ID 추출
        String memberId = null;
        
        if (authentication.getPrincipal() instanceof CustomOAuth2User) {
            memberId = ((CustomOAuth2User) authentication.getPrincipal()).getMemberId();
        } 
        
        // 권한 정보 문자열로 변환 (쉼표로 구분)
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(memberId) // Member ID를 subject로 사용
                .claim("auth", authorities)             // 권한 정보
                .setIssuedAt(new Date(now))            // 발행 시간
                .setExpiration(validity)               // 만료 시간
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

        // jWT 토큰 생성 및 반환 (권한 정보 제외)
        return Jwts.builder()
                .setSubject(authentication.getName())  // 토큰 제목 (사용자 ID)
                .setIssuedAt(new Date(now))            // 발행 시간
                .setExpiration(validity)               // 만료 시간
                .signWith(key, SignatureAlgorithm.HS512)  // 서명 알고리즘
                .compact();
    }

    /**
     * JWT 토큰에서 인증 정보 추출
     *
     * @param token JWT 토큰
     * @return Spring Security Authentication 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        
        String memberId = claims.getSubject();
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth", String.class).split(","))
                      .map(SimpleGrantedAuthority::new)
                      .toList();
        
        try {
            // ObjectId 변환 시도
            ObjectId objectId = new ObjectId(memberId);
            Member member = memberRepository.findById(objectId).orElse(null);
            
            if (member != null) {
                return new UsernamePasswordAuthenticationToken(member, token, authorities);
            }
        } catch (Exception e) {
            log.error("Member ID 변환 오류: {}", e.getMessage());
            // 여기서 ID로 찾지 못할 경우 대안으로 이메일이나 닉네임으로 시도해볼 수 있음
            // Optional<Member> memberByEmail = memberRepository.findByEmail(memberId);
            // if (memberByEmail.isPresent()) { return new ... }
        }
        
        // 모든 시도가 실패하면 기본 Authentication 반환 (또는 요구사항에 따라 null도 가능)
        return new UsernamePasswordAuthenticationToken(
            new User(memberId, "", authorities), token, authorities);
    }

    /**
     * 토큰 유효성 검증
     *
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
     *
     * @param token JWT 토큰
     * @return 사용자 이름
     */
    public String getUsernameFromToken(String token) {
        try {
            // 토큰에서 사용자 이름 추출
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

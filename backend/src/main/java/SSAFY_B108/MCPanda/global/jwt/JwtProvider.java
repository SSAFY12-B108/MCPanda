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

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    // JWT 서명에 사용할 키
    private final Key key;

    // access Token의 유효기간 (밀리초 단위)
    private final long accessTokenValidityInMilliseconds;

    // refresh Token의 유효기간 (밀리초 단위)
    private final long refreshTokenValidityInMilliseconds;

    /**
     * 설정 값을 주입받아 초기화하는 생성자
     *
     * @param secretKey JWT 서명에 사용할 비밀 키
     * @param accessTokenValidityInSeconds AccessToken 유효 시간 (초 단위)
     * @param refreshTokenValidityInSeconds RefreshToken 유효 시간 (초 단위)
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-validity-in-seconds}") long accessTokenValidityInSeconds,
            @Value("${jwt.refresh-token-validity-in-seconds}") long refreshTokenValidityInSeconds) {
        // 비밀 키를 바이트 배열로 변환하여 HMAC SHA 키 생성
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        
        // 초 단위를 밀리초 단위로 변환
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInSeconds * 1000;
    }

    /**
     * AccessToken 생성
     *
     * @param authentication 인증 정보
     * @return 생성된 JWT 토큰 문자열
     */

    public String createAccessToken(Authentication authentication) {
        // 권한 정보 문자열로 변환 (쉼표로 구분)
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        // JWT 토큰 생성 및 반환
        return Jwts.builder()
                .setSubject(authentication.getName())  // 토큰 제목 (사용자 ID)
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
        // 토큰에서 클레임(내용) 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        // 권한 정보 추출 및 GrantedAuthority 객체로 변환
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth",String.class).split(","))
                        .map(SimpleGrantedAuthority::new)
                        .toList(); // 불변 리스트의 경우 사용가능(JWT는 불변한 리스트임)

        User principal = new User(claims.getSubject(),"",authorities);

        // Authentication 객체 생성 및 반환
        return new UsernamePasswordAuthenticationToken(principal,token,authorities);
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
     * 만료된 토큰도 검증 (RefreshToken 검증용)
     *
     * @param token 검증할 JWT 토큰
     * @return 서명이 유효하면 true, 아니면 false
     */
    public boolean validateTokenIgnoringExpiration(String token) {
        try {
            // 토큰 파싱 시도 (만료 검사 무시)
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            // 만료 예외는 무시하고 true 반환
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // 기타 예외는 false 반환
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

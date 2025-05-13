package SSAFY_B108.MCPanda.domain.auth.service;

import SSAFY_B108.MCPanda.domain.auth.entity.RefreshToken;
import SSAFY_B108.MCPanda.domain.auth.repository.RefreshTokenRepository;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

/**
 * RefreshToken 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.access-token-validity-in-seconds}")
    private long accessTokenValidityInSeconds;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;


    /**
     * RefreshToken을 MongoDB에 저장
     * @param memberId (MongoDB ObjectId 문자열)
     * @param token (RefreshToken 값)
     */
    public void saveRefreshToken(String memberId, String token) {
        // refreshToken 유효기간: 14일
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenValidityInSeconds);

        // 기존 토큰 찾기
        Optional<RefreshToken> existingToken = refreshTokenRepository.findByMemberId(memberId);

        if(existingToken.isPresent()) {
            // 기존 토큰 업데이트
            RefreshToken tokenEntity = existingToken.get();
            tokenEntity.updateToken(token);
            tokenEntity.updateExpiryDate(expiryDate);
            refreshTokenRepository.save(tokenEntity);
            log.info("기존 RefreshToken 업데이트: {}", memberId);
        } else {
            // 새 토큰 생성
            RefreshToken newRefreshToken = RefreshToken.builder()
                    .memberId(memberId)
                    .token(token)
                    .expiryDate(expiryDate)
                    .build();
            refreshTokenRepository.save(newRefreshToken);
            log.info("새 RefreshToken 저장: {}", memberId);
        }
    }

    /**
     *  RefreshToken 검증
     */
    public Optional<String> validateRefreshToken(String token) {
        // 1. JWT 토큰 자체의 유효성 확인
        if(!jwtTokenProvider.validateToken(token)) {
            log.warn("유효하지 않은 리프레시 토큰 형식 또는 서명: {}", token);
            return Optional.empty();
        }

        // 2. DB에서 토큰 조회
        Optional<RefreshToken> refreshTokenOpt = refreshTokenRepository.findByToken(token);

        if(refreshTokenOpt.isEmpty()) {
            log.warn("DB에서 리프레시 토큰을 찾을 수 없음: {}", token);
            return Optional.empty();
        }

        RefreshToken refreshToken = refreshTokenOpt.get();

        // 3. 토큰 만료 여부 확인
        if(refreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("리프레시 토큰이 만료됨: {}", token);
            refreshTokenRepository.delete(refreshToken);
            return Optional.empty();
        }

        // 4. 토큰에서 회원 ID 추출 및 일치 여부 확인 (토큰 탈취 방지)
        String getTokenByMemberId = jwtTokenProvider.getMemberIdFromToken(token);
        if(!refreshToken.getMemberId().equals(getTokenByMemberId)){
            log.warn("리프레시 토큰 회원 ID 불일치. DB 토큰 회원 ID: {}, 요청 토큰 회원 ID: {}",
                    refreshToken.getMemberId(), getTokenByMemberId);
            return Optional.empty();
        }
        return Optional.of(refreshToken.getMemberId());
    }

    /**
     * 리프레시 토큰 업데이트
     * @param memberId
     * @param newRefreshToken
     */
    public void updateRefreshToken(String memberId, String newRefreshToken) {
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenValidityInSeconds);

        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByMemberId(memberId);

        RefreshToken refreshToken;
        if(tokenOpt.isPresent()) {
            // 기존 토큰 업데이트
            refreshToken = tokenOpt.get();
            refreshToken.updateToken(newRefreshToken);
            refreshToken.updateExpiryDate(expiryDate);
        } else {
            // 새 토큰 생성
            refreshToken = RefreshToken.builder()
                    .memberId(memberId)
                    .token(newRefreshToken)
                    .expiryDate(expiryDate)
                    .build();
        }

        refreshTokenRepository.save(refreshToken);
        log.info("회원 ID: {}의 리프레시 토큰이 업데이트되었습니다.", memberId);
    }
    /**
     * RefreshToken 삭제 (로그아웃 시 사용)
     * @param memberId
     */
    public void deleteRefreshToken(String memberId){
        refreshTokenRepository.deleteByMemberId(memberId);
        log.info("RefreshToken 삭제 완료: {}",memberId);
    }

    /**
     * AccessToken을 HttpOnly 쿠키로 설정
     * @param response
     * @param token
     */
    public void setAccessTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("accessToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge((int)accessTokenValidityInSeconds); // AccessToken 유효기간: 1시간

        cookie.setAttribute("SameSite", "Strict");
        
        response.addCookie(cookie);
        log.info("AccessToken 쿠키 설정 완료");
    }

    /**
     * RefreshToken을 HttpOnly 쿠키로 설정
     * @param response
     * @param token
     */
    public void setRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // HTTPS에서만 전송
        cookie.setPath("/api/auth/reissue"); // RefreshToken 재발급 요청 시에만 필요
        cookie.setMaxAge((int)refreshTokenValidityInSeconds); // RefreshToken 유효기간: 14일 (초 단위)

        cookie.setAttribute("SameSite", "Strict"); // CSRF 방지를 위해 Strict

        response.addCookie(cookie);
        log.info("RefreshToken 쿠키 설정 완료");
    }

    /**
     * AccessToken 쿠키 삭제
     * @param response
     */
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("accessToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
        log.info("AccessToken 쿠키 삭제 완료");
    }

    /**
     * RefreshToken 쿠키 삭제
     * @param response
     */
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/reissue"); // 생성 시 설정한 경로와 동일해야 함
        cookie.setMaxAge(0); // 쿠키 즉시 만료
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
        log.info("RefreshToken 쿠키 삭제 완료");
    }
}

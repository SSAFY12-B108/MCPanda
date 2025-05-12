package SSAFY_B108.MCPanda.domain.auth.service;

import SSAFY_B108.MCPanda.domain.auth.entity.RefreshToken;
import SSAFY_B108.MCPanda.domain.auth.repository.RefreshTokenRepository;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    /**
     * RefreshToken을 MongoDB에 저장
     * @param memberId (MongoDB ObjectId 문자열)
     * @param token (RefreshToken 값)
     */
    public void saveRefreshToken(String memberId, String token) {
        // refreshToken 유효기간: 14일
        Instant expiryDate = Instant.now().plus(14, ChronoUnit.DAYS);

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
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);

        if(refreshToken.isPresent() && refreshToken.get().getExpiryDate().isAfter(Instant.now())) {
            return Optional.of(refreshToken.get().getMemberId());
        }

        return Optional.empty();
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
        cookie.setMaxAge(3600); // AccessToken 유효기간: 1시간

        cookie.setAttribute("SameSite", "Lax");
        
        response.addCookie(cookie);
        log.info("AccessToken 쿠키 설정 완료");
    }
}

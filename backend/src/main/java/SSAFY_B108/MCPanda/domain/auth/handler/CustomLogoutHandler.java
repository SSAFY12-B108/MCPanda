package SSAFY_B108.MCPanda.domain.auth.handler;

import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.CustomOAuth2User;
import SSAFY_B108.MCPanda.domain.auth.service.RefreshTokenService;
import SSAFY_B108.MCPanda.global.jwt.JwtTokenProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {

    private final RefreshTokenService refreshTokenService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        log.info("CustomLogoutHandler: 사용자의 로그아웃을 시도합니다.");

        // 요청 헤더나 쿠키에서 AccessToken을 직접 읽어 처리하는 부분
        String accessToken = null;
        // 먼저 Authorization 헤더에서 토큰 추출 시도
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            accessToken = bearerToken.substring(7);
        } else {
            // 헤더에 없다면 쿠키에서 추출 시도
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        accessToken = cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (StringUtils.hasText(accessToken)) {
            try {
                // AccessToken 유효성 검증 및 memberId 추출
                if (jwtTokenProvider.validateToken(accessToken)) {
                    String memberIdFromToken = jwtTokenProvider.getMemberIdFromToken(accessToken); // getMemberIdFromToken 사용 (memberId 반환)
                    log.info("로그아웃 요청에서 유효한 AccessToken 발견. 사용자 ID: {}", memberIdFromToken);

                    // 추출한 memberId로 DB의 RefreshToken 삭제 시도
                    if (StringUtils.hasText(memberIdFromToken)) {
                        log.info("AccessToken 기반으로 DB에서 memberId '{}'의 RefreshToken 삭제를 시도합니다.", memberIdFromToken);
                        refreshTokenService.deleteRefreshToken(memberIdFromToken);
                    } else {
                        log.warn("AccessToken에서 memberId를 추출하지 못했습니다.");
                    }
                } else {
                    log.info("로그아웃 요청의 AccessToken이 유효하지 않습니다.");
                }
            } catch (Exception e) {
                // ExpiredJwtException 등 발생 시에도 memberId 추출은 가능할 수 있음
                try {
                    // 만료된 토큰에서도 memberId 추출 시도
                    String memberIdFromExpiredToken = jwtTokenProvider.getMemberIdFromToken(accessToken); // getMemberIdFromToken 사용
                    if (StringUtils.hasText(memberIdFromExpiredToken)) {
                        log.info("만료된 AccessToken에서 사용자 ID 추출 성공: {}. RefreshToken 삭제 시도.", memberIdFromExpiredToken);
                        refreshTokenService.deleteRefreshToken(memberIdFromExpiredToken);
                    }
                } catch (Exception innerEx) {
                    log.warn("로그아웃 중 AccessToken 처리 오류 (만료 토큰 처리 포함): {}", e.getMessage());
                }
            }
        } else {
            log.info("로그아웃 요청에서 AccessToken을 찾을 수 없습니다.");
        }
        
        // 3. 클라이언트(브라우저)의 AccessToken, RefreshToken, JSESSION 쿠키 삭제
        refreshTokenService.deleteAccessTokenCookie(response);
        refreshTokenService.deleteRefreshTokenCookie(response);
        refreshTokenService.deleteJSESSIONIDTokenCookie(response);
        log.info("로그아웃 처리 완료. 클라이언트 쿠키가 삭제(만료)되었습니다.");
    }
}

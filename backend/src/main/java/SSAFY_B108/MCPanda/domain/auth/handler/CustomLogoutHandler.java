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

        String memberId = null;

        // 1. 현재 인증 정보(Authentication 객체)에서 사용자 식별자(memberId) 추출 시도
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomOAuth2User) {
                memberId = ((CustomOAuth2User) principal).getMemberId();
                log.info("CustomOAuth2User로부터 memberId 추출: {}", memberId);
            } else if (principal instanceof User) {
                String username = ((User) principal).getUsername();
                log.info("UserDetails로부터 username 추출: {}. DB에서 memberId 조회가 필요할 수 있음.", username);
                // 예시: memberId = memberRepository.findByEmail(username).map(Member::getId).map(ObjectId::toHexString).orElse(null);
            }

            if (memberId != null) {
                log.info("DB에서 memberId '{}'에 해당하는 RefreshToken을 삭제합니다.", memberId);
                refreshTokenService.deleteRefreshToken(memberId);
            } else {
                log.warn("인증 정보(principal)로부터 memberId를 확인할 수 없어 DB의 RefreshToken을 삭제하지 못했습니다.");
            }
        } else {
            log.info("인증된 사용자 정보(principal)가 없어 DB의 RefreshToken 삭제를 진행하지 않습니다.");
        }

        // 2. 요청 헤더나 쿠키에서 AccessToken을 직접 읽어 처리하는 부분
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
                if (jwtTokenProvider.validateToken(accessToken)) {
                    String email = jwtTokenProvider.getUsernameFromToken(accessToken);
                    log.info("로그아웃 요청에서 유효한 AccessToken 발견. 사용자: {}", email);
                    // 여기서 email을 memberId로 변환하는 로직이 필요함 (MemberRepository 사용 등)
                    // 만약 memberId를 얻었다면:
                    // refreshTokenService.deleteRefreshToken(memberIdFromEmail);
                }
            } catch (Exception e) {
                log.warn("로그아웃 중 AccessToken 검증 오류: {}", e.getMessage());
            }
        } else {
            log.info("로그아웃 요청에서 AccessToken을 찾을 수 없습니다.");
        }
        
        // 3. 클라이언트(브라우저)의 AccessToken 및 RefreshToken 쿠키 삭제
        refreshTokenService.deleteAccessTokenCookie(response);
        refreshTokenService.deleteRefreshTokenCookie(response);

        log.info("로그아웃 처리 완료. 클라이언트 쿠키가 삭제(만료)되었습니다.");
    }
}
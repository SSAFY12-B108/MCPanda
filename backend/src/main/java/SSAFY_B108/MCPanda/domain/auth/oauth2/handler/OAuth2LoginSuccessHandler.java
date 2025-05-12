package SSAFY_B108.MCPanda.domain.auth.oauth2.handler;

import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.CustomOAuth2User;
import SSAFY_B108.MCPanda.domain.auth.service.RefreshTokenService;
import SSAFY_B108.MCPanda.global.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *  OAuth2 인증 성공 시 jWT 토큰을 생성하고 쿠키에 저장하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    
    // OAuth2 인증 성공 시 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 로그인 성공: {}", authentication.getName());

        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

        // 회원 ID 가져오기 (MongoDB ObjectId)
        String memberId = oAuth2User.getMemberId();
        // JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // RefreshToken 저장
        refreshTokenService.saveRefreshToken(memberId, refreshToken);

        // AccessToken을 HttpOnly 쿠키로 설정
        refreshTokenService.setAccessTokenCookie(response, accessToken);
        // RefreshToken을 HttpOnly 쿠키로 설정
        refreshTokenService.setRefreshTokenCookie(response, refreshToken);

        // 프론트엔드 리다이렉트 URL 설정
        String targetUrl = determineTargetUrl(request, response);

        // 리다이렉트
        getRedirectStrategy().sendRedirect(request,response,targetUrl);
    }

    /**
     *  리다이렉트 URL 결정
     *  실제 프로젝트에서는 프론트엔드 URL로 변경
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        // 기본 URL (홈페이지)
        return "/";
    }
}

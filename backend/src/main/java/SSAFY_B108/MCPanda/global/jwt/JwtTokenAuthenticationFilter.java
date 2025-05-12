package SSAFY_B108.MCPanda.global.jwt;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 인증을 처리하는 필터
 * 모든 요청에 대해 한 번씩 실행되며, JWT 토큰을 검증하고 인증 정보를 설정합니다.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 모든 요청에 대해 실행되는 필터 메서드
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 헤더에서 JWT 토큰 추출
        String jwtFromHeader = resolveTokenFromHeader(request);

        // 쿠키에서 JWT 토큰 추출
        String jwtFromCookie = resolveTokenFromCookie(request);

        // 헤더 또는 쿠키에서 가져온 토큰 중 하나 선택
        String jwt = jwtFromHeader != null ? jwtFromHeader : jwtFromCookie;

        // 토큰이 존재하고 유효한 경우 인증 정보 설정
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다.", authentication.getName());
        } else {
            log.debug("유효한 토큰이 없습니다.");
        }
        // 다음 필터 실행
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    private String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
           return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 쿠키에서 토큰 추출
     */
    private String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}

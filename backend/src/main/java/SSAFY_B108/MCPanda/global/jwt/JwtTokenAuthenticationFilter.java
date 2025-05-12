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
        // 요청 정보 로깅
        log.debug("====== JWT Filter 시작: {} {} ======", request.getMethod(), request.getRequestURI());

        // 헤더에서 JWT 토큰 추출
        String jwtFromHeader = resolveTokenFromHeader(request);
        log.debug("헤더에서 추출한 토큰: {}", jwtFromHeader != null ?
                jwtFromHeader.substring(0, Math.min(10, jwtFromHeader.length())) + "..." : "null");

        // 쿠키에서 JWT 토큰 추출
        String jwtFromCookie = resolveTokenFromCookie(request);
        log.debug("쿠키에서 추출한 토큰: {}", jwtFromCookie != null ?
                jwtFromCookie.substring(0, Math.min(10, jwtFromCookie.length())) + "..." : "null");

        // 헤더 또는 쿠키에서 가져온 토큰 중 하나 선택
        String jwt = jwtFromHeader != null ? jwtFromHeader : jwtFromCookie;
        log.debug("사용할 토큰: {}", jwt != null ?
                jwt.substring(0, Math.min(10, jwt.length())) + "..." : "null");

        // 토큰 처리 로직
        if (StringUtils.hasText(jwt)) {
            try {
                if (jwtTokenProvider.validateToken(jwt)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다. 권한: {}",
                            authentication.getName(),
                            authentication.getAuthorities());
                } else {
                    log.warn("유효하지 않은 토큰입니다. 토큰: {}",
                            jwt.substring(0, Math.min(10, jwt.length())) + "...");
                }
            } catch (Exception e) {
                log.error("토큰 처리 중 예외 발생: {}", e.getMessage(), e);
            }
        } else {
            log.debug("토큰이 제공되지 않았습니다.");
        }

        // 현재 인증 상태 로깅
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("필터 통과 전 인증 상태: {}",
                currentAuth != null ?
                        currentAuth.getName() + ", 인증됨: " + currentAuth.isAuthenticated() : "인증 없음");

        // 다음 필터 실행
        filterChain.doFilter(request, response);

        // 필터 체인 완료 후 인증 상태 로깅
        Authentication afterAuth = SecurityContextHolder.getContext().getAuthentication();
        log.debug("필터 통과 후 인증 상태: {}",
                afterAuth != null ?
                        afterAuth.getName() + ", 인증됨: " + afterAuth.isAuthenticated() : "인증 없음");

        log.debug("====== JWT Filter 종료: {} {} ======", request.getMethod(), request.getRequestURI());
    }

    /**
     * Authorization 헤더에서 토큰 추출
     */
    private String resolveTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        log.debug("Authorization 헤더 값: {}", bearerToken);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(7);
            log.debug("헤더에서 추출 성공: {}", token.substring(0, Math.min(10, token.length())) + "...");
            return token;
        }
        log.debug("Authorization 헤더에서 토큰을 추출할 수 없습니다.");
        return null;
    }

    /**
     * 쿠키에서 토큰 추출
     */
    private String resolveTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            log.debug("요청에 포함된 쿠키 수: {}", cookies.length);
            for (Cookie cookie : cookies) {
                log.debug("쿠키 확인: {}={}", cookie.getName(),
                        cookie.getValue() != null ?
                                cookie.getValue().substring(0, Math.min(10, cookie.getValue().length())) + "..." : "null");

                if ("accessToken".equals(cookie.getName())) {
                    log.debug("accessToken 쿠키에서 토큰 추출 성공");
                    return cookie.getValue();
                }
            }
            log.debug("accessToken 쿠키를 찾을 수 없습니다.");
        } else {
            log.debug("요청에 쿠키가 없습니다.");
        }
        return null;
    }
}
package SSAFY_B108.MCPanda.domain.auth.controller;

import SSAFY_B108.MCPanda.domain.auth.dto.TokenDto;
import SSAFY_B108.MCPanda.domain.auth.service.RefreshTokenService;
import SSAFY_B108.MCPanda.domain.auth.service.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping ("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final RefreshTokenService refreshTokenService;

    @GetMapping("/logout")
    public ResponseEntity<Map<String,String>> logout() {
        return ResponseEntity.ok(Map.of("message", "로그아웃에 성공했습니다."));
    }

    /**
     * AccessToken과 RefreshToken을 재발급합니다.
     * @param refreshTokenValue RefreshToken 값 (쿠키에서 추출)
     * @param response HttpServletResponse 객체 (쿠키 설정을 위해)
     * @return 새로운 AccessToken과 RefreshToken (본문에도 포함하여 클라이언트가 즉시 사용 가능하도록)
     */
    @PostMapping("/reissue")
    public ResponseEntity<TokenDto> reissueTokens(
            @CookieValue(name = "refreshToken", required = false) String refreshTokenValue,
            HttpServletResponse response) {

        if (refreshTokenValue == null || refreshTokenValue.isEmpty()) {
            log.warn("Refresh token cookie is missing or empty.");
            return ResponseEntity.status(401).body(null); // Unauthorized
        }

        try {
            TokenDto newTokens = tokenService.reissueTokens(refreshTokenValue);

            // 새로운 토큰들을 HttpOnly 쿠키로 설정
            refreshTokenService.setAccessTokenCookie(response, newTokens.getAccessToken());
            refreshTokenService.setRefreshTokenCookie(response, newTokens.getRefreshToken());

            log.info("Tokens reissued and cookies set successfully.");
            // 클라이언트가 새 토큰을 즉시 사용할 수 있도록 응답 본문에도 포함 (선택 사항이지만 편리함)
            return ResponseEntity.ok(newTokens);
        } catch (RuntimeException e) {
            log.warn("Token reissue failed: {}", e.getMessage());
            // 실패 시 기존 쿠키를 삭제하는 로직을 추가할 수도 있습니다.
            // 예를 들어, 만료된 리프레시 토큰으로 요청이 온 경우 등
            // refreshTokenService.deleteRefreshTokenCookie(response); // 이런 메소드가 필요할 수 있음
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
    }
}

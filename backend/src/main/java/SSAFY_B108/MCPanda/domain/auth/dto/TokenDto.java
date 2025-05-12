package SSAFY_B108.MCPanda.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT 토큰 관련 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;

    /**
     * RefreshToken 요청을 위한 내부 클래스
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {
        private String refreshToken;
    }
}

package SSAFY_B108.MCPanda.domain.auth.service;

import SSAFY_B108.MCPanda.domain.auth.dto.TokenDto;
import SSAFY_B108.MCPanda.domain.auth.entity.RefreshToken;
import SSAFY_B108.MCPanda.domain.auth.repository.RefreshTokenRepository;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.domain.member.repository.MemberRepository;
import SSAFY_B108.MCPanda.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MemberRepository memberRepository;

    @Value("${jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenValidityInSeconds;

    /**
     * 토큰 재발급 처리 메서드
     * @param refreshTokenValue 기존 리프레시 토큰 값
     * @return 새로 발급된 액세스 토큰과 리프레시 토큰
     */
    @Transactional
    public TokenDto reissueTokens(String refreshTokenValue) {
        // 1. 리프레시 토큰 유효성 검사
        if (!jwtTokenProvider.validateToken(refreshTokenValue)) {
            log.warn("유효하지 않은 리프레시 토큰: {}", refreshTokenValue);
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다. (형식 또는 서명 오류)");
        }

        // 2. 리프레시 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUsernameFromToken(refreshTokenValue);

        // 3. DB에 저장된 리프레시 토큰 조회
        RefreshToken storedRefreshToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> {
                    log.warn("DB에서 리프레시 토큰을 찾을 수 없음: {}", refreshTokenValue);
                    return new RuntimeException("유효하지 않은 리프레시 토큰: DB에 존재하지 않습니다.");
                });

        // 4. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("토큰의 이메일에 해당하는 회원을 찾을 수 없음: {}", email);
                    return new RuntimeException("토큰에 해당하는 사용자를 찾을 수 없습니다.");
                });

        // 5. 토큰의 회원 ID와 DB에 저장된 토큰의 회원 ID 일치 여부 확인
        String memberId = member.getId().toHexString();
        if (!storedRefreshToken.getMemberId().equals(memberId)) {
            log.warn("리프레시 토큰 회원 ID 불일치. 토큰 회원 ID: {}, 조회된 회원 ID: {}",
                    storedRefreshToken.getMemberId(), memberId);
            throw new RuntimeException("유효하지 않은 리프레시 토큰: 토큰-사용자 불일치");
        }

        // 6. 리프레시 토큰 만료 여부 확인
        if (storedRefreshToken.getExpiryDate().isBefore(Instant.now())) {
            log.warn("리프레시 토큰이 만료됨: {}", refreshTokenValue);
            refreshTokenRepository.delete(storedRefreshToken); // 만료된 토큰 삭제
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }

        // 7. 인증 객체 생성
        String role = "ROLE_" + member.getRole().toUpperCase();
        User principal = new User(
                email,
                "",
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "",
                principal.getAuthorities()
        );

        // 8. 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 9. DB의 리프레시 토큰 업데이트
        Instant newExpiryDate = Instant.now().plusSeconds(refreshTokenValidityInSeconds);
        storedRefreshToken.updateToken(newRefreshToken);
        storedRefreshToken.updateExpiryDate(newExpiryDate);
        refreshTokenRepository.save(storedRefreshToken);

        log.info("회원: {}의 토큰이 재발급되었습니다.", email);
        return new TokenDto(newAccessToken, newRefreshToken);
    }
}
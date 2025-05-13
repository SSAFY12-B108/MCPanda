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
import org.bson.types.ObjectId; // ObjectId 임포트 추가
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberRepository memberRepository;

    /**
     * 토큰 재발급 처리 메서드
     * @param refreshToken 기존 리프레시 토큰 값
     * @return 새로 발급된 액세스 토큰과 리프레시 토큰
     */
    @Transactional
    public TokenDto reissueTokens(String refreshToken) {
        // 1. RefreshTokenService를 통해 리프레시 토큰 유효성 검사
        Optional<String> memberIdOpt = refreshTokenService.validateRefreshToken(refreshToken);

        if(memberIdOpt.isEmpty()) {
            log.warn("유효하지 않은 리프레시 토큰: {}", refreshToken);
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        String memberId = memberIdOpt.get();

        // 2. 회원 ID로 회원 조회 (ObjectId 변환 필요)
        Member member = memberRepository.findById(new ObjectId(memberId))
                .orElseThrow(() -> new RuntimeException("토큰에 해당하는 사용자를 찾을 수 없습니다."));

        // 3. 인증 객체 생성 (Principal로 Member 객체 사용)
        String role = "ROLE_" + member.getRole().toUpperCase();
        User principal = new User(
                member.getEmail(),
                "",
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                "",
                principal.getAuthorities()
        );

        // 4. 새 토큰 생성
        String newAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(authentication);

        // 5. DB의 리프레시 토큰 업데이트
        refreshTokenService.updateRefreshToken(memberId, newRefreshToken);

        log.info("회원: {}의 토큰이 재발급되었습니다.", member.getEmail());
        return new TokenDto(newAccessToken, newRefreshToken);
    }
}

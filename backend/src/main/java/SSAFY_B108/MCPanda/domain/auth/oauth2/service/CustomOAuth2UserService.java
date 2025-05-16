package SSAFY_B108.MCPanda.domain.auth.oauth2.service;

import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.CustomOAuth2User;
import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.OAuth2UserInfo;
import SSAFY_B108.MCPanda.domain.auth.oauth2.dto.OAuth2UserInfoFactory;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *  OAuth2 인증 후 사용자 정보를 로드하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    
    private final MemberService memberService;
    
    // OAuth2 인증 후 사용자 정보 로드
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 기본 OAuth2UserService 호출
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        log.info("OAuth2 사용자 정보: {}", oAuth2User.getAttributes());

        // OAuth2 서비스 ID (google/github)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // OAuth2 로그인 진행 시 키가 되는 필드 값 (PK)
        // Google: "sub", GitHub: "id"
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        log.info("registrationId: {}", registrationId);
        log.info("userNameAttributeName: {}", userNameAttributeName);

        // OAuth2UserInfo 객체 생성
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId, oAuth2User.getAttributes());
        
        // MemberService를 통해 회원가입 또는 정보 업데이트 처리
        Member member = memberService.saveOrUpdateOAuth2User(oAuth2UserInfo, registrationId);

        // MongoDB ObjectId를 문자열로 변환
        String memberId = member.getId().toString();

        // OAuth2User 속성에 memberId 추가
        Map<String,Object> attributes = new HashMap<>(oAuth2User.getAttributes());
        attributes.put("memberId",memberId); // <-- MongoDB ObjectId를 String으로 속성에 추가

        log.info("OAuth2 사용자 정보 로드 완료: email={}, memberId={}", oAuth2UserInfo.getEmail(), memberId);

        // CustomOAuth2User 객체 생성 및 반환
        return new CustomOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                userNameAttributeName,
                oAuth2UserInfo.getEmail(),
                oAuth2UserInfo.getName()
        );
    }
}

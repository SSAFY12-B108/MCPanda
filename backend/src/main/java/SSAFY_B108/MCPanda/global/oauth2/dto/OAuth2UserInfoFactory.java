package SSAFY_B108.MCPanda.global.oauth2.dto;

import java.util.Map;

/**
 * OAuth2 제공자에 따라 적절한 OAuth2UserInfo 구현체를 생성하는 팩토리 클래스
 */
public class OAuth2UserInfoFactory {

    /**
     * 제공자 ID와 속성 맵을 받아 적절한 OAuth2UserInfo 객체 반환
     * @param registrationId OAuth2 제공자 ID (google, github 등)
     * @param attributes 사용자 속성 맵
     * @return 해당 제공자에 맞는 OAuth2UserInfo 구현체
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        if (registrationId.equalsIgnoreCase("google")) {
            return new GoogleOAuth2UserInfo(attributes);
        } else if (registrationId.equalsIgnoreCase("github")) {
            return new GitHubOAuth2UserInfo(attributes);
        } else {
            throw new IllegalArgumentException("지원하지 않는 OAuth2 제공자입니다: " +registrationId);
        }
    }
}

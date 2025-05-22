package SSAFY_B108.MCPanda.domain.auth.oauth2.dto;

import java.util.Map;

/**
 *  oAuth2 제공자로부터 받은 사용자 정보를 추상화한 클래스
 */
public abstract class OAuth2UserInfo {
    
    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getEmail();

    public abstract String getImageUrl();

}

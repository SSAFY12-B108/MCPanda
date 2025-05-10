package SSAFY_B108.MCPanda.global.oauth2.dto;

import java.util.Map;

/**
 * Google oAuth2 제공자로부터 받은 사용자 정보
 */
public class GoogleOAuth2UserInfo extends OAuth2UserInfo{

    public GoogleOAuth2UserInfo(Map<String,Object> attributes) {
        super(attributes);
    }
    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }
}

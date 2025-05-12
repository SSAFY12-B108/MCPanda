package SSAFY_B108.MCPanda.global.oauth2.dto;

import java.util.Map;

/**
 *  GitHub oAuth2 제공자로부터 받은 사용자 정보
 */
public class GitHubOAuth2UserInfo extends OAuth2UserInfo{

    public GitHubOAuth2UserInfo(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id")); // GitHub API는 userID를 int 또는 Integer 타입으로 제공
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("login");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}

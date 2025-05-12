package SSAFY_B108.MCPanda.global.oauth2.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

/**
 * 커스텀 OAuth2User 구현체
 * 기본 DefaultOAuth2User를 확장하여 이메일과 이름 필드 추가
 */
@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final String email;
    private final String name;
    // 생성자
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, String name) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.name = name;
    }

    // 편의 메서드: MongoDB ObjectId 반환
    public String getMemberId() {
        return (String) this.getAttributes().get("memberId");
    }
}

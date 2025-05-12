package SSAFY_B108.MCPanda.domain.article.entity;

public class Author {
    private String memberId; // MongoDB의 ObjectId를 문자열로 표현
    private String nickname;

    // 기본 생성자
    public Author() {
    }

    // 모든 필드를 받는 생성자
    public Author(String memberId, String nickname) {
        this.memberId = memberId;
        this.nickname = nickname;
    }

    // Getter와 Setter
    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

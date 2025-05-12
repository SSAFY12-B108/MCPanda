package SSAFY_B108.MCPanda.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "게시글을 찾을 수 없습니다.") // reason을 추가할 수 있습니다.
public class ArticleNotFoundException extends RuntimeException {
    public ArticleNotFoundException(String message) {
        super(message);
    }

    // 기본 메시지를 사용하는 생성자 (선택 사항)
    public ArticleNotFoundException() {
        super("요청하신 리소스를 찾을 수 없습니다.");
    }
}

package SSAFY_B108.MCPanda.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "수정 권한이 없습니다.") // reason을 추가할 수 있습니다.
public class UnauthorizedOperationException extends RuntimeException {
    public UnauthorizedOperationException(String message) {
        super(message);
    }

    // 기본 메시지를 사용하는 생성자 (선택 사항)
    public UnauthorizedOperationException() {
        super("해당 작업을 수행할 권한이 없습니다.");
    }
}

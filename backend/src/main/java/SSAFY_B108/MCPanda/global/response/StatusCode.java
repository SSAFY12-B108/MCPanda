package SSAFY_B108.MCPanda.global.response;

import lombok.Getter;

/**
 * API 응답 상태 코드를 관리하는 열거형
 */
@Getter
public enum StatusCode {
    // 성공 코드 (2xx)
    OK(200, "성공적으로 처리되었습니다."),
    CREATED(201, "리소스가 성공적으로 생성되었습니다."),

    // 클라이언트 오류 코드 (4xx)
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    NOT_FOUND(404, "요청한 리소스를 찾을 수 없습니다."),
    CONFLICT(409, "요청이 현재 서버 상태와 충돌합니다."),

    // 서버 오류 코드 (5xx)
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    SERVICE_UNAVAILABLE(503, "서비스를 사용할 수 없습니다.");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
package SSAFY_B108.MCPanda.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

/**
 * 모든 API 응답의 공통 형식을 위한 wrapper 클래스
 * @param <T> 응답 데이터의 타입
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    // Getter 메소드들
    private final int code;
    private final String message;
    private final T data;

    // 성공 응답 생성 메소드 (데이터 있음)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(ApiStatusCode.OK.getCode(), ApiStatusCode.OK.getMessage(), data);
    }

    // 성공 응답 생성 메소드 (데이터 없음)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(ApiStatusCode.OK.getCode(), ApiStatusCode.OK.getMessage(), null);
    }

    // 성공 응답 생성 메소드 (커스텀 메시지)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(ApiStatusCode.OK.getCode(), message, data);
    }

    // 상태 코드 지정 응답 생성 메소드
    public static <T> ApiResponse<T> of(ApiStatusCode apiStatusCode, T data) {
        return new ApiResponse<>(apiStatusCode.getCode(), apiStatusCode.getMessage(), data);
    }

    // 상태 코드 지정 응답 생성 메소드 (데이터 없음)
    public static <T> ApiResponse<T> of(ApiStatusCode apiStatusCode) {
        return new ApiResponse<>(apiStatusCode.getCode(), apiStatusCode.getMessage(), null);
    }
    
    // 상태 코드와 메시지 커스텀 응답 생성 메소드
    public static <T> ApiResponse<T> of(ApiStatusCode apiStatusCode, String message, T data) {
        return new ApiResponse<>(apiStatusCode.getCode(), message, data);
    }

    // 실패 응답 생성 메소드
    public static <T> ApiResponse<T> error(ApiStatusCode apiStatusCode) {
        return new ApiResponse<>(apiStatusCode.getCode(), apiStatusCode.getMessage(), null);
    }

    // 실패 응답 생성 메소드 (커스텀 메시지)
    public static <T> ApiResponse<T> error(ApiStatusCode apiStatusCode, String message) {
        return new ApiResponse<>(apiStatusCode.getCode(), message, null);
    }

    // 생성자
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

}
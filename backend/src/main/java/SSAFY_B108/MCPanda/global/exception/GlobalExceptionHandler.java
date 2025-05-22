package SSAFY_B108.MCPanda.global.exception;

import SSAFY_B108.MCPanda.global.response.ApiResponse;
import SSAFY_B108.MCPanda.global.response.ApiStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

/**
 * 전역 예외 처리를 담당하는 클래스
 * 모든 컨트롤러에서 발생하는 예외를 일관된 형식으로 처리합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * CustomException 처리
     */
    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ApiResponse<Object>> handleCustomException(CustomException e) {
        log.error("CustomException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.valueOf(e.getApiStatusCode().getCode()))
                .body(ApiResponse.error(e.getApiStatusCode(), e.getMessage()));
    }

    /**
     * 유효성 검사 실패 시 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException", e);
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ApiStatusCode.BAD_REQUEST, message != null ? message : "입력값이 올바르지 않습니다."));
    }

    /**
     * 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Object>> handleBindException(BindException e) {
        log.error("BindException", e);
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ApiStatusCode.BAD_REQUEST, message != null ? message : "입력값이 올바르지 않습니다."));
    }

    /**
     * 메소드 인자 타입 불일치 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.error("MethodArgumentTypeMismatchException", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ApiStatusCode.BAD_REQUEST, "입력값의 타입이 올바르지 않습니다."));
    }

    /**
     * 제약 조건 위반 예외 처리
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<ApiResponse<Object>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException", e);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ApiStatusCode.BAD_REQUEST, e.getMessage()));
    }

    /**
     * NoSuchElementException 처리 (데이터를 찾을 수 없을 때)
     */
    @ExceptionHandler(NoSuchElementException.class)
    protected ResponseEntity<ApiResponse<Object>> handleNoSuchElementException(NoSuchElementException e) {
        log.error("NoSuchElementException", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ApiStatusCode.NOT_FOUND, e.getMessage()));
    }

    /**
     * 기타 모든 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ApiStatusCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."));
    }
} 
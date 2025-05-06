package SSAFY_B108.MCPanda.global.exception;

import SSAFY_B108.MCPanda.global.response.StatusCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final StatusCode statusCode;
    
    public CustomException(StatusCode statusCode) {
        super(statusCode.getMessage());
        this.statusCode = statusCode;
    }
    
    public CustomException(StatusCode statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }
} 
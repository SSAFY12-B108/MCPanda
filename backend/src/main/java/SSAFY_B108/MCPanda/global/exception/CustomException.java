package SSAFY_B108.MCPanda.global.exception;

import SSAFY_B108.MCPanda.global.response.ApiStatusCode;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    
    private final ApiStatusCode apiStatusCode;
    
    public CustomException(ApiStatusCode apiStatusCode) {
        super(apiStatusCode.getMessage());
        this.apiStatusCode = apiStatusCode;
    }
    
    public CustomException(ApiStatusCode apiStatusCode, String message) {
        super(message);
        this.apiStatusCode = apiStatusCode;
    }
} 
package SSAFY_B108.MCPanda.domain.example.controller;

import SSAFY_B108.MCPanda.domain.example.dto.UserDto;
import SSAFY_B108.MCPanda.global.response.ApiResult;
import SSAFY_B108.MCPanda.global.response.StatusCode;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ApiResult 사용 예시를 보여주는 컨트롤러
 */
@RestController
@RequestMapping("/api/examples")
public class ExampleController {

    // 간단한 문자열 데이터 응답 예시
    @GetMapping("/simple")
    public ApiResult<String> getSimpleResponse() {
        return ApiResult.success("Hello, API Response!");
    }

    // 사용자 정보 응답 예시
    @GetMapping("/user")
    public ApiResult<UserDto> getUser() {
        UserDto user = new UserDto("user123", "홍길동", "example@gmail.com", "https://example.com/profile.jpg");
        return ApiResult.success(user);
    }

    // 사용자 목록 응답 예시
    @GetMapping("/users")
    public ApiResult<List<UserDto>> getUsers() {
        List<UserDto> users = Arrays.asList(
            new UserDto("user123", "홍길동", "hong@example.com", "https://example.com/hong.jpg"),
            new UserDto("user456", "김철수", "kim@example.com", "https://example.com/kim.jpg"),
            new UserDto("user789", "이영희", "lee@example.com", "https://example.com/lee.jpg")
        );
        return ApiResult.of(StatusCode.OK, users);
    }

    // 페이지네이션 응답 예시
    @GetMapping("/users/paged")
    public ApiResult<Map<String, Object>> getPagedUsers(@RequestParam(defaultValue = "1") int page) {
        List<UserDto> users = Arrays.asList(
            new UserDto("user123", "홍길동", "hong@example.com", "https://example.com/hong.jpg"),
            new UserDto("user456", "김철수", "kim@example.com", "https://example.com/kim.jpg")
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("page", page);
        response.put("totalPages", 3);
        response.put("totalUsers", 6);
        response.put("users", users);
        
        return ApiResult.success(response);
    }

    // 에러 응답 예시
    @GetMapping("/user/{id}")
    public ApiResult<UserDto> getUserById(@PathVariable String id) {
        if ("error".equals(id)) {
            return ApiResult.error(StatusCode.NOT_FOUND, "사용자 ID를 찾을 수 없습니다: " + id);
        }
        
        UserDto user = new UserDto(id, "홍길동", "hong@example.com", "https://example.com/hong.jpg");
        return ApiResult.success(user);
    }

    // 리소스 생성 예시
    @PostMapping("/user")
    public ApiResult<UserDto> createUser(@RequestBody UserDto userDto) {
        // 사용자 생성 로직 (생략)
        return ApiResult.of(StatusCode.CREATED, "사용자가 성공적으로 생성되었습니다.", userDto);
    }
} 
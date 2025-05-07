package SSAFY_B108.MCPanda;

import org.springframework.web.bind.annotation.GetMapping;
// Spring Web 관련 어노테이션을 사용하기 위한 import
// @GetMapping: HTTP GET 요청을 특정 핸들러 메서드에 매핑하기 위한 어노테이션
// @RestController: JSON/XML 형태의 응답을 반환하는 REST API 컨트롤러임을 명시
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Spring MCPanda!";
    }
} 
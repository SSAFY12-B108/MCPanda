package SSAFY_B108.MCPanda;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
// Spring Web 관련 어노테이션을 사용하기 위한 import
// @GetMapping: HTTP GET 요청을 특정 핸들러 메서드에 매핑하기 위한 어노테이션
// @RestController: JSON/XML 형태의 응답을 반환하는 REST API 컨트롤러임을 명시

@RestController // 이 클래스는 REST API 요청을 처리하고, 응답 본문에 직접 데이터를 씁니다.
public class GoodbyeController {
    @GetMapping("/goodbye")
    public String goodbye() {
        return "Goodbye, Spring MCPanda!";
    }
}

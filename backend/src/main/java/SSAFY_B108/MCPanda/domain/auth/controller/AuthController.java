package SSAFY_B108.MCPanda.domain.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping ("/api/auth")
public class AuthController {

    @GetMapping("/login")
    public ResponseEntity<Map<String,String>> login() {
        return ResponseEntity.ok(Map.of(
                "message", "OAuth2 로그인 페이지입니다.",
                "google_auth_url", "/api/auth/oauth2/authorization/google",
                "github_auth_url", "/api/auth/oauth2/authorization/github"
        ));
    }

    @GetMapping("/success")
    public ResponseEntity<Map<String,String>> success() {
        return ResponseEntity.ok(Map.of("message","로그인에 성공했습니다."));
    }

    @GetMapping("/failure")
    public ResponseEntity<Map<String,String>> failure() {
        return ResponseEntity.ok(Map.of("message","로그인에 실패했습니다."));
    }
}

package SSAFY_B108.MCPanda.domain.member.controller;

import SSAFY_B108.MCPanda.domain.member.dto.MemberResponseDto;
import SSAFY_B108.MCPanda.domain.member.entity.Member;
import SSAFY_B108.MCPanda.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponseDto> getCurrentMember() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).build();
        }

        Object principal = authentication.getPrincipal();
        log.info("Principal 타입: {}", principal.getClass().getName());

        Member memberPrincipal = (Member) principal;
        String email = memberPrincipal.getEmail();
        log.info("Principal에서 Member 이메일 추출: {}", email);

        MemberResponseDto memberDto = memberService.getMemberByEmail(email);
        return ResponseEntity.ok(memberDto);
    }
}

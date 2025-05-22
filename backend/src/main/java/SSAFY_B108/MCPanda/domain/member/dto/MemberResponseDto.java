package SSAFY_B108.MCPanda.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private String id;
    private String email;
    private String name;
    private String nickname;
    private String profileImage;
    private String role;
    private Set<String> connectedProviders;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

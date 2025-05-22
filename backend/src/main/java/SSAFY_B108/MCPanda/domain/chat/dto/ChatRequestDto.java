package SSAFY_B108.MCPanda.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "챗봇 요청 DTO")
public class ChatRequestDto {
    @Schema(description = "사용자 ID", example = "user123")
    private String userId;

    @Schema(description = "사용자 메시지", example = "오늘 날씨 어때?")
    private String message;
} 
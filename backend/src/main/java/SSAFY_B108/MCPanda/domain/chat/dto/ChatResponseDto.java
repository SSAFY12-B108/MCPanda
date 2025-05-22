package SSAFY_B108.MCPanda.domain.chat.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "챗봇 응답 DTO")
public class ChatResponseDto {
    @Schema(description = "챗봇 응답 메시지", example = "오늘은 맑고 따뜻한 날씨입니다.")
    private String response;
} 
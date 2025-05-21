package SSAFY_B108.MCPanda.domain.chat.controller;

import SSAFY_B108.MCPanda.domain.chat.dto.ChatRequestDto;
import SSAFY_B108.MCPanda.domain.chat.dto.ChatResponseDto;
import SSAFY_B108.MCPanda.domain.chat.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "챗봇 API", description = "AI 챗봇 상호작용을 위한 API")
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @Operation(summary = "챗봇 메시지 전송", description = "사용자 메시지를 챗봇에게 전송하고 응답을 받습니다.")
    @ApiResponse(responseCode = "200", description = "챗봇 응답 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ChatResponseDto.class)))
    @PostMapping("/chat")
    public ResponseEntity<ChatResponseDto> sendMessageToChatbot(@RequestBody ChatRequestDto requestDto) {
        ChatResponseDto responseDto = chatService.getChatResponse(requestDto);
        return ResponseEntity.ok(responseDto);
    }
} 
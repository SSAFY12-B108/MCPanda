package SSAFY_B108.MCPanda.domain.chat.service;

import SSAFY_B108.MCPanda.domain.chat.dto.ChatRequestDto;
import SSAFY_B108.MCPanda.domain.chat.dto.ChatResponseDto;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    public ChatResponseDto getChatResponse(ChatRequestDto requestDto) {
        // 실제 AI 모델 연동 로직은 여기에 구현
        // 현재는 고정된 응답 반환
        String userMessage = requestDto.getMessage();
        String botResponse;

        if (userMessage != null && userMessage.contains("날씨")) {
            botResponse = "오늘은 맑고 따뜻한 날씨입니다.";
        } else if (userMessage != null && userMessage.contains("안녕")) {
            botResponse = "안녕하세요! 무엇을 도와드릴까요?";
        }
        else {
            botResponse = "죄송해요, 잘 이해하지 못했어요.";
        }
        return new ChatResponseDto(botResponse);
    }
} 
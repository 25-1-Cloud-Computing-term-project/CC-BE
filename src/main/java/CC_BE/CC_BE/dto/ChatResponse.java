package CC_BE.CC_BE.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * 챗봇 응답을 위한 DTO
 */
@Getter
@Builder
public class ChatResponse {
    private String message;           // 응답 상태 메시지
    private String answer;            // LLM이 생성한 답변
    private List<String> images;      // base64로 인코딩된 이미지 리스트

    public static ChatResponse fromMLServerResponse(MLServerChatResponse mlResponse) {
        return ChatResponse.builder()
                .message(mlResponse.getMessage())
                .answer(mlResponse.getAnswer())
                .images(mlResponse.getImages())
                .build();
    }
} 
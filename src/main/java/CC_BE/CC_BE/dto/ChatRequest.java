package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 챗봇 질문 요청을 위한 DTO
 */
@Getter
@Setter
public class ChatRequest {
    private Long modelId;      // 질문할 모델의 ID
    private String question;   // 사용자의 질문
} 
package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.dto.ChatRequest;
import CC_BE.CC_BE.dto.ChatResponse;
import CC_BE.CC_BE.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 챗봇 Q&A API를 제공하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    /**
     * 챗봇에 질문을 전송하고 답변을 받습니다.
     * 
     * @param request 질문 요청 (모델 ID와 질문 내용)
     * @return 챗봇의 답변 (텍스트와 이미지)
     */
    @PostMapping("/manual")
    public ResponseEntity<ChatResponse> askQuestion(@RequestBody ChatRequest request) {
        try {
            log.info("챗봇 질문 요청 - 모델 ID: {}, 질문: {}", request.getModelId(), request.getQuestion());
            ChatResponse response = chatService.processQuestion(request.getModelId(), request.getQuestion());
            log.info("챗봇 응답 완료 - 모델 ID: {}", request.getModelId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("챗봇 응답 실패 - 모델 ID: {}, 에러: {}", request.getModelId(), e.getMessage(), e);
            throw e;
        }
    }
} 
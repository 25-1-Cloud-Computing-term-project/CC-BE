package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 챗봇 Q&A 기능을 제공하는 서비스 클래스
 */
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ProductModelService productModelService;
    private final MLServerService mlServerService;

    /**
     * 사용자의 질문에 대한 답변을 생성합니다.
     * 1. 모델 정보를 조회합니다.
     * 2. ML 서버에 질문을 전송합니다.
     * 3. ML 서버의 응답을 반환합니다.
     *
     * @param modelId 질문할 모델의 ID
     * @param question 사용자의 질문
     * @return 생성된 답변과 관련 이미지
     * @throws RuntimeException 모델을 찾을 수 없거나 ML 서버 오류 발생 시
     */
    public ChatResponse processQuestion(Long modelId, String question) {
        // 모델 정보 조회
        ProductModel model = productModelService.findById(modelId);
        if (model == null) {
            throw new RuntimeException("모델을 찾을 수 없습니다.");
        }

        // ML 서버에 질문 전송 및 응답 수신
        return mlServerService.askQuestion(model.getName(), question);
    }
} 
package CC_BE.CC_BE.service;

import CC_BE.CC_BE.config.MLServerProperties;
import CC_BE.CC_BE.dto.ChatResponse;
import CC_BE.CC_BE.dto.MLServerChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ML 서버와의 통신을 담당하는 서비스 클래스
 * 매뉴얼 PDF 파일을 ML 서버로 전송하고 처리 결과를 받아오는 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class MLServerService {
    private final RestTemplate mlServerRestTemplate;
    private final MLServerProperties mlServerProperties;
    private static final Logger log = LoggerFactory.getLogger(MLServerService.class);

    /**
     * 매뉴얼 PDF 파일을 ML 서버에 업로드하고 처리를 요청합니다.
     * 
     * @param file 업로드할 PDF 파일
     * @param modelName 모델의 이름 (ML 서버에서 문서 식별자로 사용)
     * @return ML 서버의 처리 성공 여부
     * @throws IOException 파일 처리 중 오류 발생 시
     */
    public boolean uploadManualToMLServer(MultipartFile file, String modelName) throws IOException {
        String url = mlServerProperties.getUrl() + "/api/manuals/upload";
        log.info("ML 서버로 파일 업로드 요청 시작");
        log.info("URL: {}", url);
        log.info("모델명: {}", modelName);
        log.info("파일명: {}", file.getOriginalFilename());
        log.info("파일 크기: {} bytes", file.getSize());
        log.info("Content-Type: {}", file.getContentType());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("x-api-key", mlServerProperties.getApiKey());
            log.info("요청 헤더 설정 완료: {}", headers);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());
            body.add("doc_name", modelName);
            log.info("요청 바디 설정 완료");

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            log.info("ML 서버로 요청 전송 시작");

            ResponseEntity<MLServerResponse> response = mlServerRestTemplate.postForEntity(
                    url, requestEntity, MLServerResponse.class);
            
            log.info("ML 서버 응답 수신");
            log.info("응답 상태 코드: {}", response.getStatusCode());
            log.info("응답 헤더: {}", response.getHeaders());
            log.info("응답 바디: {}", response.getBody());

            String msg = response.getBody() != null ? response.getBody().getMessage() : null;
            boolean success = msg != null && (
                "completed".equalsIgnoreCase(msg) ||
                "PDF uploaded successfully".equalsIgnoreCase(msg)
            );
            
            log.info("ML 서버 처리 결과: {}", success ? "성공" : "실패");
            return success;
        } catch (Exception e) {
            log.error("ML 서버 요청 중 예외 발생", e);
            throw e;
        }
    }

    /**
     * 챗봇 질문을 ML 서버에 전송하고 응답을 받아옵니다.
     * 
     * @param modelName 질문할 모델의 이름
     * @param question 사용자의 질문
     * @return ML 서버의 응답 (텍스트 답변과 이미지 포함)
     */
    public ChatResponse askQuestion(String modelName, String question) {
        String url = mlServerProperties.getUrl() + "/api/chat/manual";
        log.info("ML 서버로 챗봇 질문 요청 시작");
        log.info("URL: {}", url);
        log.info("모델명: {}", modelName);
        log.info("질문: {}", question);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("x-api-key", mlServerProperties.getApiKey());
            log.info("요청 헤더 설정 완료: {}", headers);

            Map<String, String> body = Map.of(
                "doc_name", modelName,
                "question", question
            );
            log.info("요청 바디 설정 완료: {}", body);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);
            log.info("ML 서버로 요청 전송 시작");

            ResponseEntity<MLServerChatResponse> response = mlServerRestTemplate.postForEntity(
                    url, requestEntity, MLServerChatResponse.class);
            
            log.info("ML 서버 응답 수신");
            log.info("응답 상태 코드: {}", response.getStatusCode());
            log.info("응답 헤더: {}", response.getHeaders());
            log.info("응답 바디: {}", response.getBody());

            if (response.getBody() == null) {
                throw new RuntimeException("ML 서버로부터 응답을 받지 못했습니다.");
            }

            MLServerChatResponse mlResponse = response.getBody();
            log.info("ML 서버 응답 변환 완료 - message: {}, answer 길이: {}, 이미지 개수: {}", 
                mlResponse.getMessage(),
                mlResponse.getAnswer() != null ? mlResponse.getAnswer().length() : 0,
                mlResponse.getImages() != null ? mlResponse.getImages().size() : 0);

            return ChatResponse.fromMLServerResponse(mlResponse);
        } catch (Exception e) {
            log.error("ML 서버 챗봇 요청 중 예외 발생", e);
            throw new RuntimeException("ML 서버와 통신 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * ML 서버의 매뉴얼 업로드 응답을 담는 내부 클래스
     */
    private static class MLServerResponse {
        private String doc_name;    // 처리된 문서의 이름
        private String message;     // 처리 결과 메시지

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDoc_name() {
            return doc_name;
        }

        public void setDoc_name(String doc_name) {
            this.doc_name = doc_name;
        }
    }
} 
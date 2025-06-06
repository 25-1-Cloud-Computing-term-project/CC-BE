package CC_BE.CC_BE.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.boot.web.client.RestTemplateCustomizer;

/**
 * ML 서버와의 통신을 위한 설정 클래스
 * ML 서버 연결 설정 및 통신에 필요한 Bean들을 정의합니다.
 */
@Configuration
public class MLServerConfig {

    /**
     * ML 서버와의 HTTP 통신을 위한 RestTemplate Bean을 생성합니다.
     * - 연결 타임아웃: 30초
     * - 읽기 타임아웃: 5분 (PDF 처리 시간 고려)
     * @return ML 서버 통신용 RestTemplate 객체
     */
    @Bean
    public RestTemplate mlServerRestTemplate() {
        return new RestTemplateBuilder()
            .customizers((RestTemplateCustomizer) restTemplate -> {
                if (restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory factory) {
                    factory.setConnectTimeout(30_000); // 30초
                    factory.setReadTimeout(300_000);   // 5분
                }
            })
            .build();
    }
} 
package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 제품 모델 생성/수정 요청 DTO
 * 공용 모델의 경우 name과 categoryId가 필수
 * 개인 모델의 경우 name만 필수
 */
@Getter @Setter
public class ProductModelRequest {
    /**
     * 제품 모델의 이름 (필수)
     */
    private String name;

    /**
     * 제품 모델이 속할 카테고리의 ID (공용 모델인 경우에만 사용)
     */
    private Long categoryId;
} 
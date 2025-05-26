package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 브랜드 생성/수정 요청 DTO
 */
@Getter @Setter
public class BrandRequest {
    /**
     * 브랜드의 이름
     */
    private String name;
} 
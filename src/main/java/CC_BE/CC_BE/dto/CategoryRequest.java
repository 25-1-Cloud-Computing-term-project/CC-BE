package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 카테고리 생성/수정 요청 DTO
 */
@Getter @Setter
public class CategoryRequest {
    /**
     * 카테고리의 이름
     */
    private String name;

    /**
     * 카테고리가 속할 브랜드의 ID
     */
    private Long brandId;
} 
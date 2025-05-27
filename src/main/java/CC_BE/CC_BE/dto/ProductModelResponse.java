package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.ProductModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductModelResponse {
    private Long id;
    private String name;
    
    private SimpleCategory category;
    private SimpleBrand brand;
    private UserResponse owner;
    private ManualResponse manual;

    @Getter
    @AllArgsConstructor
    public static class SimpleCategory {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class SimpleBrand {
        private Long id;
        private String name;
    }

    public static ProductModelResponse from(ProductModel model) {
        SimpleCategory simpleCategory = null;
        SimpleBrand simpleBrand = null;

        // 공용 모델인 경우에만 카테고리와 브랜드 정보를 설정
        if (model.getCategory() != null) {
            simpleCategory = new SimpleCategory(
                model.getCategory().getId(),
                model.getCategory().getName()
            );
            
            if (model.getCategory().getBrand() != null) {
                simpleBrand = new SimpleBrand(
                    model.getCategory().getBrand().getId(),
                    model.getCategory().getBrand().getName()
                );
            }
        }

        return new ProductModelResponse(
            model.getId(),
            model.getName(),
            simpleCategory,
            simpleBrand,
            model.getOwner() != null ? UserResponse.from(model.getOwner()) : null,
            model.getManual() != null ? ManualResponse.from(model.getManual()) : null
        );
    }
} 
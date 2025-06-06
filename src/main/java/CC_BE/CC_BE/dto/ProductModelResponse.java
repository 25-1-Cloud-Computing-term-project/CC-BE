package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.ProductModel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductModelResponse {
    private Long id;
    private String name;
    private CategoryResponse category;
    private BrandResponse brand;
    private UserResponse owner;
    private ManualResponse manual;

    public static ProductModelResponse from(ProductModel model) {
        return ProductModelResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .category(model.getCategory() != null ? CategoryResponse.from(model.getCategory()) : null)
                .brand(model.getBrand() != null ? BrandResponse.from(model.getBrand()) : null)
                .owner(model.getOwner() != null ? UserResponse.from(model.getOwner()) : null)
                .manual(model.getManual() != null ? ManualResponse.from(model.getManual()) : null)
                .build();
    }
} 
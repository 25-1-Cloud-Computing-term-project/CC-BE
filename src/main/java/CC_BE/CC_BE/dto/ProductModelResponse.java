package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.ProductModel;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProductModelResponse {
    private Long id;
    private String name;
    private CategoryResponse category;
    private BrandResponse brand;
    private UserResponse owner;
    private ManualResponse manual;

    public static ProductModelResponse from(ProductModel model) {
        ProductModelResponse response = new ProductModelResponse();
        response.setId(model.getId());
        response.setName(model.getName());
        
        if (model.getCategory() != null) {
            response.setCategory(CategoryResponse.from(model.getCategory()));
        }
        
        if (model.getBrand() != null) {
            response.setBrand(BrandResponse.from(model.getBrand()));
        }
        
        if (model.getOwner() != null) {
            response.setOwner(UserResponse.from(model.getOwner()));
        }
        
        if (model.getManual() != null) {
            response.setManual(ManualResponse.from(model.getManual()));
        }
        
        return response;
    }
} 
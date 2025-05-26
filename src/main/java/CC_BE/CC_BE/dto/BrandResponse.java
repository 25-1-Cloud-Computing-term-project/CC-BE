package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.Brand;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BrandResponse {
    private Long id;
    private String name;

    public static BrandResponse from(Brand brand) {
        BrandResponse response = new BrandResponse();
        response.setId(brand.getId());
        response.setName(brand.getName());
        return response;
    }
} 
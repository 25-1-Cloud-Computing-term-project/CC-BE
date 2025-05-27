package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.Brand;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {
    private Long id;
    private String name;
    private List<CategoryResponse> categories;

    public static BrandResponse from(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            null
        );
    }

    public static BrandResponse fromWithCategories(Brand brand) {
        return new BrandResponse(
            brand.getId(),
            brand.getName(),
            brand.getCategories() != null ? 
                brand.getCategories().stream()
                    .map(CategoryResponse::from)
                    .collect(Collectors.toList()) : 
                null
        );
    }
} 
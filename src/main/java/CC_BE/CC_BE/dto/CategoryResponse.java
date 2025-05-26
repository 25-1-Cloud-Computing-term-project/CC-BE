package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.Category;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CategoryResponse {
    private Long id;
    private String name;

    public static CategoryResponse from(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        return response;
    }
} 
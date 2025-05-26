package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.Brand;
import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.repository.BrandRepository;
import CC_BE.CC_BE.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final BrandService brandService;

    /**
     * 모든 카테고리 목록을 조회합니다.
     * @return 전체 카테고리 목록
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    /**
     * 특정 카테고리를 ID로 조회합니다.
     * @param id 조회할 카테고리의 ID
     * @return 조회된 카테고리 정보
     * @throws IllegalArgumentException 카테고리를 찾을 수 없는 경우
     */
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
    }

    /**
     * 특정 브랜드에 속한 카테고리 목록을 조회합니다.
     * @param brandId 조회할 브랜드의 ID
     * @return 해당 브랜드의 카테고리 목록
     * @throws IllegalArgumentException 브랜드를 찾을 수 없는 경우
     */
    public List<Category> getCategoriesByBrand(Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        return categoryRepository.findByBrand(brand);
    }

    /**
     * 새로운 카테고리를 생성합니다.
     * @param name 생성할 카테고리의 이름
     * @param brandId 카테고리가 속할 브랜드의 ID
     * @return 생성된 카테고리 정보
     * @throws IllegalArgumentException 브랜드를 찾을 수 없는 경우
     */
    @Transactional
    public Category createCategory(String name, Long brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        Category category = new Category();
        category.setName(name);
        category.setBrand(brand);
        return categoryRepository.save(category);
    }

    /**
     * 특정 카테고리의 정보를 수정합니다.
     * @param id 수정할 카테고리의 ID
     * @param name 변경할 카테고리의 새로운 이름
     * @param brandId 변경할 브랜드의 ID
     * @return 수정된 카테고리 정보
     */
    @Transactional
    public Category updateCategory(Long id, String name, Long brandId) {
        Category category = getCategoryById(id);
        category.setName(name);
        
        if (brandId != null) {
            Brand brand = brandService.getBrandById(brandId);
            category.setBrand(brand);
        }
        
        return categoryRepository.save(category);
    }

    /**
     * 특정 카테고리를 삭제합니다.
     * @param id 삭제할 카테고리의 ID
     */
    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}

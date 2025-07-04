package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.Brand;
import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.repository.BrandRepository;
import CC_BE.CC_BE.repository.ProductModelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandService {
    private final BrandRepository brandRepository;
    private final ProductModelRepository productModelRepository;

    /**
     * 새로운 브랜드를 생성합니다.
     * @param name 생성할 브랜드의 이름
     * @return 생성된 브랜드 정보
     */
    @Transactional
    public Brand createBrand(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        return brandRepository.save(brand);
    }

    /**
     * 모든 브랜드 목록을 조회합니다.
     * @return 전체 브랜드 목록
     */
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    /**
     * 특정 브랜드를 ID로 조회합니다.
     * @param id 조회할 브랜드의 ID
     * @return 조회된 브랜드 정보
     * @throws IllegalArgumentException 브랜드를 찾을 수 없는 경우
     */
    public Brand getBrandById(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));
    }

    /**
     * 특정 브랜드의 정보를 수정합니다.
     * @param id 수정할 브랜드의 ID
     * @param name 변경할 브랜드의 새로운 이름
     * @return 수정된 브랜드 정보
     */
    @Transactional
    public Brand updateBrand(Long id, String name) {
        Brand brand = brandRepository.findById(id).orElseThrow();
        brand.setName(name);
        return brandRepository.save(brand);
    }

    /**
     * 특정 브랜드를 삭제합니다.
     * 브랜드 삭제 시 연관된 카테고리와 제품 모델도 함께 삭제됩니다.
     * @param id 삭제할 브랜드의 ID
     */
    @Transactional
    public void deleteBrand(Long id) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Brand not found"));

        // 브랜드에 속한 모든 카테고리의 제품 모델 삭제
        for (Category category : brand.getCategories()) {
            List<ProductModel> models = productModelRepository.findByCategory(category);
            productModelRepository.deleteAll(models);
        }

        // 브랜드 삭제 (cascade로 인해 카테고리도 자동 삭제됨)
        brandRepository.delete(brand);
    }
}

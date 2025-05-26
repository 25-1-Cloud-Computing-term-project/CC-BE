package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.Brand;
import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.repository.CategoryRepository;
import CC_BE.CC_BE.repository.ProductModelRepository;
import CC_BE.CC_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductModelService {
    private final ProductModelRepository productModelRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ManualService manualService;

    /**
     * 모든 공용 모델을 조회합니다.
     * @return 전체 공용 모델 목록
     */
    public List<ProductModel> getAllPublicModels() {
        log.debug("Fetching all public models");
        List<ProductModel> models = productModelRepository.findByOwnerIsNull();
        log.debug("Found {} public models", models.size());
        return models;
    }

    /**
     * 새로운 공용 모델을 생성합니다.
     * @param name 생성할 모델의 이름
     * @param categoryId 모델이 속할 카테고리의 ID
     * @return 생성된 공용 모델 정보
     * @throws IllegalArgumentException 카테고리를 찾을 수 없는 경우
     */
    @Transactional
    public ProductModel createPublicModel(String name, Long categoryId) {
        log.debug("Creating new public model with name: {}, categoryId: {}", name, categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        ProductModel model = new ProductModel();
        model.setName(name);
        model.setCategory(category);
        model.setBrand(category.getBrand());
        model.setOwner(null); // 공용 모델
        
        return productModelRepository.save(model);
    }

    /**
     * 새로운 개인 모델을 생성합니다.
     * @param name 생성할 모델의 이름
     * @param userId 모델 소유자의 ID
     * @return 생성된 개인 모델 정보
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public ProductModel createPersonalModel(String name, Long userId) {
        log.debug("Creating new personal model with name: {}, userId: {}", name, userId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        ProductModel model = new ProductModel();
        model.setName(name);
        model.setOwner(owner);
        // 개인 모델은 브랜드와 카테고리를 설정하지 않음
        
        return productModelRepository.save(model);
    }

    /**
     * 특정 카테고리에 속한 공용 모델을 조회합니다.
     * @param categoryId 조회할 카테고리의 ID
     * @return 해당 카테고리의 공용 모델 목록
     * @throws IllegalArgumentException 카테고리를 찾을 수 없는 경우
     */
    public List<ProductModel> getPublicModelsByCategory(Long categoryId) {
        log.debug("Fetching public models for category: {}", categoryId);
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        List<ProductModel> models = productModelRepository.findByCategoryAndOwnerIsNull(category);
        log.debug("Found {} public models for category {}", models.size(), categoryId);
        return models;
    }

    /**
     * 공용 모델의 정보를 수정합니다.
     * @param id 수정할 모델의 ID
     * @param name 변경할 모델의 새로운 이름
     * @param categoryId 변경할 카테고리의 ID
     * @return 수정된 공용 모델 정보
     * @throws IllegalArgumentException 모델이나 카테고리를 찾을 수 없는 경우
     * @throws IllegalStateException 개인 모델을 수정하려는 경우
     */
    @Transactional
    public ProductModel updatePublicModel(Long id, String name, Long categoryId) {
        log.debug("Updating public model: {}", id);
        ProductModel model = productModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));
        
        if (model.getOwner() != null) {
            throw new IllegalStateException("개인 모델은 이 방식으로 수정할 수 없습니다.");
        }

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
                
        model.setName(name);
        model.setCategory(category);
        model.setBrand(category.getBrand());
        
        return productModelRepository.save(model);
    }

    /**
     * 개인 모델의 정보를 수정합니다.
     */
    @Transactional
    public ProductModel updatePersonalModel(Long id, String name, Long userId) {
        log.debug("Updating personal model: {}", id);
        ProductModel model = productModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));
        
        if (model.getOwner() == null || !model.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("해당 모델을 수정할 권한이 없습니다.");
        }
        
        model.setName(name);
        return productModelRepository.save(model);
    }

    /**
     * 모든 모델을 조회합니다. (관리자 전용)
     */
    public List<ProductModel> getAllModels() {
        log.debug("Fetching all models");
        List<ProductModel> models = productModelRepository.findAll();
        log.debug("Found {} models", models.size());
        return models;
    }

    /**
     * 개인 모델을 삭제합니다. (소유자만 가능)
     */
    @Transactional
    public void deletePersonalModel(Long id, Long userId) {
        log.debug("Starting deletion of personal model with ID: {} by user: {}", id, userId);
        
        ProductModel model = productModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));

        // 개인 모델이 아니거나 소유자가 아닌 경우
        if (model.getOwner() == null || !model.getOwner().getId().equals(userId)) {
            throw new IllegalStateException("해당 모델을 삭제할 권한이 없습니다.");
        }
        
        // 매뉴얼이 있다면 삭제
        if (model.getManual() != null) {
            manualService.deleteManual(model.getId(), model.getOwner());
        }
        
        productModelRepository.deleteById(id);
        log.debug("Personal model deletion completed");
    }

    /**
     * 모델을 삭제합니다. (관리자 전용)
     */
    @Transactional
    public void deleteModelByAdmin(Long id) {
        log.debug("Starting deletion of model with ID: {} by admin", id);
        
        ProductModel model = productModelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Model not found"));
        
        // 매뉴얼이 있다면 삭제
        if (model.getManual() != null) {
            manualService.deleteManual(model.getId(), model.getOwner());
        }
        
        productModelRepository.deleteById(id);
        log.debug("Model deletion by admin completed");
    }

    /**
     * 특정 사용자의 모든 개인 모델을 조회합니다.
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 개인 모델 목록
     * @throws IllegalArgumentException 사용자를 찾을 수 없는 경우
     */
    public List<ProductModel> getUserModels(Long userId) {
        log.debug("Fetching models for user: {}", userId);
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        List<ProductModel> models = productModelRepository.findByOwner(owner);
        log.debug("Found {} models for user {}", models.size(), userId);
        return models;
    }

    /**
     * 특정 모델을 ID로 조회합니다.
     * @param id 조회할 모델의 ID
     * @return 조회된 모델 정보
     * @throws RuntimeException 모델을 찾을 수 없는 경우
     */
    public ProductModel findById(Long id) {
        return productModelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("모델을 찾을 수 없습니다: " + id));
    }
}
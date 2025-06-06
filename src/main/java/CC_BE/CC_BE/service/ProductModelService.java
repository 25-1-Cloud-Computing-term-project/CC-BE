package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.Brand;
import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.domain.Manual;
import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.ProductModelResponse;
import CC_BE.CC_BE.repository.CategoryRepository;
import CC_BE.CC_BE.repository.ProductModelRepository;
import CC_BE.CC_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private final MLServerService mlServerService;

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
     * 1. 모델명 유효성을 검사합니다.
     * 2. 매뉴얼 PDF를 ML 서버로 전송하여 처리를 요청합니다.
     * 3. 매뉴얼 파일을 로컬 스토리지에 저장합니다.
     * 4. 공용 모델 정보를 데이터베이스에 저장합니다.
     *
     * @param name 생성할 모델의 이름 (한글 불가, 3글자 이상)
     * @param categoryId 모델이 속할 카테고리의 ID
     * @param manualFile 모델의 매뉴얼 PDF 파일
     * @return 생성된 공용 모델 정보
     * @throws RuntimeException 모델 생성 중 오류 발생 시
     */
    @Transactional
    public ProductModelResponse createPublicModel(String name, Long categoryId, MultipartFile manualFile) {
        // 모델명 유효성 검사
        validateModelName(name);

        // 카테고리 조회
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("카테고리를 찾을 수 없습니다."));

        try {
            // ML 서버에 매뉴얼 업로드
            boolean mlServerSuccess = mlServerService.uploadManualToMLServer(manualFile, name);
            if (!mlServerSuccess) {
                throw new RuntimeException("ML 서버 처리 중 오류가 발생했습니다.");
            }

            // 공용 모델 생성
            ProductModel productModel = ProductModel.builder()
                    .name(name)
                    .category(category)
                    .brand(category.getBrand())
                    .build();

            // 모델 저장
            ProductModel savedModel = productModelRepository.save(productModel);

            // 매뉴얼 저장 및 모델과 연결
            Manual savedManual = manualService.saveManual(manualFile, name);
            savedManual.setProductModel(savedModel);  // 매뉴얼과 모델 연결
            
            // 모델에도 매뉴얼 설정
            savedModel.setManual(savedManual);
            
            // 최종 저장
            savedModel = productModelRepository.save(savedModel);
            
            return ProductModelResponse.from(savedModel);
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 새로운 개인 모델을 생성합니다.
     * 1. 모델명 유효성을 검사합니다.
     * 2. 매뉴얼 PDF를 ML 서버로 전송하여 처리를 요청합니다.
     * 3. 매뉴얼 파일을 로컬 스토리지에 저장합니다.
     * 4. 개인 모델 정보를 데이터베이스에 저장합니다.
     *
     * @param name 생성할 모델의 이름 (한글 불가, 3글자 이상)
     * @param manualFile 모델의 매뉴얼 PDF 파일
     * @param userEmail 모델 소유자의 이메일
     * @return 생성된 개인 모델 정보
     * @throws RuntimeException 모델 생성 중 오류 발생 시
     */
    @Transactional
    public ProductModelResponse createPersonalModel(String name, MultipartFile manualFile, String userEmail) {
        // 모델명 유효성 검사
        validateModelName(name);

        // 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        try {
            // ML 서버에 매뉴얼 업로드
            boolean mlServerSuccess = mlServerService.uploadManualToMLServer(manualFile, name);
            if (!mlServerSuccess) {
                throw new RuntimeException("ML 서버 처리 중 오류가 발생했습니다.");
            }

            // 개인 모델 생성
            ProductModel productModel = ProductModel.builder()
                    .name(name)
                    .owner(user)
                    .build();

            // 모델 저장
            ProductModel savedModel = productModelRepository.save(productModel);

            // 매뉴얼 저장 및 모델과 연결
            Manual savedManual = manualService.saveManual(manualFile, name);
            savedManual.setProductModel(savedModel);  // 매뉴얼과 모델 연결
            savedManual.setUploader(user);  // 매뉴얼 업로더 설정
            
            // 모델에도 매뉴얼 설정
            savedModel.setManual(savedManual);
            
            // 최종 저장
            savedModel = productModelRepository.save(savedModel);

            return ProductModelResponse.from(savedModel);
        } catch (IOException e) {
            throw new RuntimeException("파일 처리 중 오류가 발생했습니다.", e);
        }
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

    /**
     * 모델명의 유효성을 검사합니다.
     * 1. 모델명이 null이거나 비어있지 않은지 확인
     * 2. 모델명이 3글자 이상인지 확인
     * 3. 모델명에 한글이 포함되어 있지 않은지 확인
     * 4. 이미 존재하는 모델명인지 확인
     *
     * @param name 검사할 모델명
     * @throws IllegalArgumentException 유효성 검사 실패 시
     */
    private void validateModelName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("모델명은 필수입니다.");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("모델명은 3글자 이상이어야 합니다.");
        }
        if (name.matches(".*[ㄱ-ㅎㅏ-ㅣ가-힣].*")) {
            throw new IllegalArgumentException("모델명에 한글을 사용할 수 없습니다.");
        }
        if (productModelRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 모델명입니다.");
        }
    }
}
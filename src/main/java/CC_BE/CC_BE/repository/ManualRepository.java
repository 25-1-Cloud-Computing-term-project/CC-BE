package CC_BE.CC_BE.repository;

import CC_BE.CC_BE.domain.Manual;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.domain.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 매뉴얼 엔티티에 대한 데이터 접근 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공
 */
@Repository
public interface ManualRepository extends JpaRepository<Manual, Long> {
    /**
     * 특정 사용자가 업로드한 모든 매뉴얼을 조회합니다.
     * @param uploader 조회할 업로더(사용자)
     * @return 해당 사용자가 업로드한 매뉴얼 목록
     */
    List<Manual> findByUploader(User uploader);

    /**
     * 특정 제품 모델의 매뉴얼을 조회합니다.
     * @param productModel 조회할 제품 모델
     * @return 해당 제품 모델의 매뉴얼 목록
     */
    List<Manual> findByProductModel(ProductModel productModel);

    Optional<Manual> findByProductModelId(Long modelId);
}

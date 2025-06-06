package CC_BE.CC_BE.repository;

import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 제품 모델 엔티티에 대한 데이터 접근 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공
 */
@Repository
public interface ProductModelRepository extends JpaRepository<ProductModel, Long> {
    /**
     * 소유자가 없는 모든 공용 모델을 조회합니다.
     * @return 공용 모델 목록
     */
    List<ProductModel> findByOwnerIsNull();

    /**
     * 특정 사용자가 소유한 모든 개인 모델을 조회합니다.
     * @param owner 조회할 소유자
     * @return 해당 사용자의 개인 모델 목록
     */
    List<ProductModel> findByOwner(User owner);

    /**
     * 특정 카테고리에 속한 모든 모델을 조회합니다.
     * @param category 조회할 카테고리
     * @return 해당 카테고리의 모델 목록
     */
    List<ProductModel> findByCategory(Category category);

    /**
     * 특정 카테고리에 속한 공용 모델을 조회합니다.
     * @param category 조회할 카테고리
     * @return 해당 카테고리의 공용 모델 목록
     */
    List<ProductModel> findByCategoryAndOwnerIsNull(Category category);

    /**
     * 주어진 이름의 모델이 존재하는지 확인합니다.
     * @param name 확인할 모델 이름
     * @return 존재 여부
     */
    boolean existsByName(String name);
}

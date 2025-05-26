package CC_BE.CC_BE.repository;

import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 카테고리 엔티티에 대한 데이터 접근 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    /**
     * 특정 브랜드에 속한 모든 카테고리를 조회합니다.
     * @param brand 조회할 브랜드
     * @return 해당 브랜드의 카테고리 목록
     */
    List<Category> findByBrand(Brand brand);
}

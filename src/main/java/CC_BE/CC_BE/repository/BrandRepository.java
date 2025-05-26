package CC_BE.CC_BE.repository;

import CC_BE.CC_BE.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 브랜드 엔티티에 대한 데이터 접근 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
}

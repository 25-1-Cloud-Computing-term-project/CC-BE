package CC_BE.CC_BE.repository;

import CC_BE.CC_BE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 사용자 엔티티에 대한 데이터 접근 인터페이스
 * JpaRepository를 상속받아 기본적인 CRUD 기능을 제공
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이메일로 사용자를 조회합니다.
     * @param email 조회할 사용자의 이메일
     * @return Optional로 감싸진 사용자 정보
     */
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}

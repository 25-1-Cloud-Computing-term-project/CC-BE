package CC_BE.CC_BE.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * 사용자 엔티티
 * 시스템의 사용자 정보를 관리
 */
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    /**
     * 사용자의 고유 식별자
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 사용자의 이메일 (로그인 아이디로 사용)
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 사용자의 비밀번호 (암호화되어 저장)
     */
    @Column(nullable = false)
    private String password;

    /**
     * 사용자의 역할 (ROLE_USER, ROLE_ADMIN 등)
     */
    @Column(nullable = false)
    private String role;  // ROLE_USER, ROLE_ADMIN
}

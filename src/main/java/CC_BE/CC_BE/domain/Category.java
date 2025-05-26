package CC_BE.CC_BE.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

/**
 * 카테고리 엔티티
 * 제품 모델의 카테고리 정보를 관리
 */
@Entity
@Table(name = "category")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Category {
    /**
     * 카테고리의 고유 식별자
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 카테고리의 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 카테고리가 속한 브랜드
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = false)
    @JsonIgnoreProperties({"categories", "hibernateLazyInitializer", "handler"})
    private Brand brand;
}

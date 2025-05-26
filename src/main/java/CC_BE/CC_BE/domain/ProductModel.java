package CC_BE.CC_BE.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

/**
 * 제품 모델 엔티티
 * 공용 모델과 개인 모델을 구분하여 관리
 */
@Entity
@Table(name = "product_model")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProductModel {
    /**
     * 제품 모델의 고유 식별자
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 제품 모델의 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 제품 모델의 카테고리 (공용 모델에만 사용)
     * 개인 모델의 경우 null
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = true)
    @JsonIgnoreProperties({"brand", "hibernateLazyInitializer", "handler"})
    private Category category;

    /**
     * 제품 모델의 브랜드 (공용 모델에만 사용)
     * 개인 모델의 경우 null
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "brand_id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Brand brand;

    /**
     * 제품 모델의 소유자
     * 공용 모델의 경우 null, 개인 모델의 경우 해당 사용자
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User owner;

    /**
     * 제품 모델의 매뉴얼 정보
     */
    @OneToOne(mappedBy = "productModel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"productModel", "hibernateLazyInitializer", "handler"})
    private Manual manual;
}

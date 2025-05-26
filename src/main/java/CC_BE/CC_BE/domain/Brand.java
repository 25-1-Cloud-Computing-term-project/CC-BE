package CC_BE.CC_BE.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * 브랜드 엔티티
 * 제품 모델의 브랜드 정보를 관리
 */
@Entity
@Table(name = "brand")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Brand {
    /**
     * 브랜드의 고유 식별자
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 브랜드의 이름
     */
    @Column(nullable = false)
    private String name;

    /**
     * 브랜드에 속한 카테고리 목록
     */
    @OneToMany(mappedBy = "brand", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"brand", "hibernateLazyInitializer", "handler"})
    private List<Category> categories;
}

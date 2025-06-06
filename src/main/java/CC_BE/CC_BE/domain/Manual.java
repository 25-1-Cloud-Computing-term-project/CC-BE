package CC_BE.CC_BE.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 매뉴얼 엔티티
 * 제품 모델의 매뉴얼 파일 정보를 관리
 */
@Entity
@Table(name = "manual")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Manual {
    /**
     * 매뉴얼의 고유 식별자
     */
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 매뉴얼 파일의 원본 이름
     */
    @Column(nullable = false)
    private String fileName;

    /**
     * 매뉴얼 파일의 저장 경로
     */
    @Column(nullable = false)
    private String filePath;

    /**
     * 매뉴얼 업로드 일시
     */
    private LocalDateTime uploadDate;

    /**
     * 매뉴얼을 업로드한 사용자
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User uploader;

    /**
     * 매뉴얼이 속한 제품 모델
     */
    @OneToOne
    @JoinColumn(name = "product_model_id")
    @JsonIgnoreProperties({"manual", "hibernateLazyInitializer", "handler"})
    private ProductModel productModel;

    /**
     * 매뉴얼이 속한 제품 모델의 이름
     */
    @Column(nullable = false)
    private String modelName;

    /**
     * 매뉴얼이 ML 처리되었는지 여부
     */
    @Column(nullable = false)
    private boolean mlProcessed;
}

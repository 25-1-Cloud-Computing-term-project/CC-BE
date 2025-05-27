package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.Brand;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.BrandRequest;
import CC_BE.CC_BE.dto.BrandResponse;
import CC_BE.CC_BE.dto.CommonResponse;
import CC_BE.CC_BE.security.CustomUserDetails;
import CC_BE.CC_BE.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    /**
     * 모든 브랜드 조회
     * 공용 브랜드만 조회 가능
     */
    @GetMapping
    public ResponseEntity<List<BrandResponse>> getAllBrands() {
        List<BrandResponse> brands = brandService.getAllBrands().stream()
                .map(BrandResponse::fromWithCategories)
                .collect(Collectors.toList());
        return ResponseEntity.ok(brands);
    }

    /**
     * 특정 브랜드 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandResponse> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(BrandResponse.fromWithCategories(brandService.getBrandById(id)));
    }

    /**
     * 새로운 브랜드 생성 (관리자 전용)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<BrandResponse>> createBrand(
            @RequestBody BrandRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("브랜드 생성 요청 - 이름: {}, 관리자: {}", request.getName(), user.getId());
            Brand brand = brandService.createBrand(request.getName());
            return ResponseEntity.ok(CommonResponse.of("브랜드가 성공적으로 생성되었습니다.", BrandResponse.from(brand)));
        } catch (Exception e) {
            log.error("브랜드 생성 실패 - 이름: {}, 에러: {}", request.getName(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.of("브랜드 생성 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 브랜드 정보 수정 (관리자 전용)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<BrandResponse>> updateBrand(
            @PathVariable Long id,
            @RequestBody BrandRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("브랜드 수정 요청 - ID: {}, 이름: {}, 관리자: {}", id, request.getName(), user.getId());
            Brand brand = brandService.updateBrand(id, request.getName());
            return ResponseEntity.ok(CommonResponse.of("브랜드가 성공적으로 수정되었습니다.", BrandResponse.from(brand)));
        } catch (Exception e) {
            log.error("브랜드 수정 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.of("브랜드 수정 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 브랜드 삭제 (관리자 전용)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> deleteBrand(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("브랜드 삭제 요청 - ID: {}, 관리자: {}", id, user.getId());
            brandService.deleteBrand(id);
            return ResponseEntity.ok(CommonResponse.of("브랜드가 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("브랜드 삭제 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.of("브랜드 삭제 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}

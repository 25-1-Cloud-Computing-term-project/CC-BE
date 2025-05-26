package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.Brand;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.BrandRequest;
import CC_BE.CC_BE.security.CustomUserDetails;
import CC_BE.CC_BE.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

    /**
     * 특정 브랜드 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Long id) {
        return ResponseEntity.ok(brandService.getBrandById(id));
    }

    /**
     * 새로운 브랜드 생성 (관리자 전용)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> createBrand(
            @RequestBody BrandRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("브랜드 생성 요청 - 이름: {}, 관리자: {}", request.getName(), user.getId());
        return ResponseEntity.ok(brandService.createBrand(request.getName()));
    }

    /**
     * 브랜드 정보 수정 (관리자 전용)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Brand> updateBrand(
            @PathVariable Long id,
            @RequestBody BrandRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("브랜드 수정 요청 - ID: {}, 이름: {}, 관리자: {}", id, request.getName(), user.getId());
        return ResponseEntity.ok(brandService.updateBrand(id, request.getName()));
    }

    /**
     * 브랜드 삭제 (관리자 전용)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBrand(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("브랜드 삭제 요청 - ID: {}, 관리자: {}", id, user.getId());
        brandService.deleteBrand(id);
        return ResponseEntity.ok().build();
    }
}

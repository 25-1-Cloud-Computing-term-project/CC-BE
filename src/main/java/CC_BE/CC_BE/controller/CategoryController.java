package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.CategoryRequest;
import CC_BE.CC_BE.dto.CategoryResponse;
import CC_BE.CC_BE.dto.CommonResponse;
import CC_BE.CC_BE.security.CustomUserDetails;
import CC_BE.CC_BE.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 모든 카테고리 조회
     * 공용 카테고리만 조회 가능
     */
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<CategoryResponse> response = categoryService.getAllCategories().stream()
            .map(CategoryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 카테고리 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(CategoryResponse.from(categoryService.getCategoryById(id)));
    }

    /**
     * 특정 브랜드의 카테고리 목록 조회
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<CategoryResponse>> getCategoriesByBrand(@PathVariable Long brandId) {
        List<CategoryResponse> response = categoryService.getCategoriesByBrand(brandId).stream()
            .map(CategoryResponse::from)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 카테고리 생성 (관리자 전용)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<CategoryResponse>> createCategory(
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("카테고리 생성 요청 - 이름: {}, 브랜드: {}, 관리자: {}", 
                request.getName(), request.getBrandId(), user.getId());
            Category category = categoryService.createCategory(request.getName(), request.getBrandId());
            return ResponseEntity.ok(CommonResponse.of("카테고리가 성공적으로 생성되었습니다.", CategoryResponse.from(category)));
        } catch (Exception e) {
            log.error("카테고리 생성 실패 - 이름: {}, 브랜드: {}, 에러: {}", 
                request.getName(), request.getBrandId(), e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.of("카테고리 생성 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 카테고리 정보 수정 (관리자 전용)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("카테고리 수정 요청 - ID: {}, 이름: {}, 브랜드: {}, 관리자: {}", 
                id, request.getName(), request.getBrandId(), user.getId());
            Category category = categoryService.updateCategory(id, request.getName(), request.getBrandId());
            return ResponseEntity.ok(CommonResponse.of("카테고리가 성공적으로 수정되었습니다.", CategoryResponse.from(category)));
        } catch (Exception e) {
            log.error("카테고리 수정 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.of("카테고리 수정 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 카테고리 삭제 (관리자 전용)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("카테고리 삭제 요청 - ID: {}, 관리자: {}", id, user.getId());
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(CommonResponse.of("카테고리가 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("카테고리 삭제 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(CommonResponse.of("카테고리 삭제 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}

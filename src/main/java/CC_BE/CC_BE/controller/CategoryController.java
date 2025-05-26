package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.Category;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.CategoryRequest;
import CC_BE.CC_BE.security.CustomUserDetails;
import CC_BE.CC_BE.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    private Map<String, Object> convertToResponse(Category category) {
        return Map.of(
            "id", category.getId(),
            "name", category.getName(),
            "brandId", category.getBrand().getId()
        );
    }

    /**
     * 모든 카테고리 조회
     * 공용 카테고리만 조회 가능
     */
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        List<Map<String, Object>> response = categoryService.getAllCategories().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 카테고리 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(convertToResponse(categoryService.getCategoryById(id)));
    }

    /**
     * 특정 브랜드의 카테고리 목록 조회
     */
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Map<String, Object>>> getCategoriesByBrand(@PathVariable Long brandId) {
        List<Map<String, Object>> response = categoryService.getCategoriesByBrand(brandId).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 새로운 카테고리 생성 (관리자 전용)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createCategory(
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("카테고리 생성 요청 - 이름: {}, 브랜드: {}, 관리자: {}", 
            request.getName(), request.getBrandId(), user.getId());
        return ResponseEntity.ok(convertToResponse(
            categoryService.createCategory(request.getName(), request.getBrandId())
        ));
    }

    /**
     * 카테고리 정보 수정 (관리자 전용)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("카테고리 수정 요청 - ID: {}, 이름: {}, 브랜드: {}, 관리자: {}", 
            id, request.getName(), request.getBrandId(), user.getId());
        return ResponseEntity.ok(convertToResponse(
            categoryService.updateCategory(id, request.getName(), request.getBrandId())
        ));
    }

    /**
     * 카테고리 삭제 (관리자 전용)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("카테고리 삭제 요청 - ID: {}, 관리자: {}", id, user.getId());
        categoryService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }
}

package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.*;
import CC_BE.CC_BE.security.CustomUserDetails;
import CC_BE.CC_BE.service.ProductModelService;
import CC_BE.CC_BE.service.ManualService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/models")
@RequiredArgsConstructor
public class ProductModelController {
    private final ProductModelService productModelService;
    private final ManualService manualService;

    /**
     * 모든 공용 모델 조회
     */
    @GetMapping("/public")
    public ResponseEntity<List<ProductModelResponse>> getAllPublicModels() {
        log.info("공용 모델 조회 요청");
        List<ProductModelResponse> response = productModelService.getAllPublicModels().stream()
                .map(ProductModelResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 공용 모델 생성 (관리자 전용)
     */
    @PostMapping("/public")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<ProductModelResponse>> createPublicModel(
            @RequestParam("name") String name,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("manualFile") MultipartFile manualFile) {
        try {
            if (manualFile == null || manualFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(CommonResponse.of("매뉴얼 파일은 필수입니다.", null));
            }

            String filename = manualFile.getOriginalFilename();
            String contentType = manualFile.getContentType();
            
            if (filename == null || (!filename.toLowerCase().endsWith(".pdf")) || 
                (contentType != null && !contentType.toLowerCase().contains("pdf"))) {
                return ResponseEntity.badRequest()
                        .body(CommonResponse.of("PDF 파일만 업로드 가능합니다.", null));
            }

            log.info("공용 모델 생성 요청 - 이름: {}, 카테고리: {}", name, categoryId);
            ProductModelResponse model = productModelService.createPublicModel(name, categoryId, manualFile);
            log.info("공용 모델 생성 성공 - ID: {}", model.getId());

            return ResponseEntity.ok(CommonResponse.of("공용 모델이 성공적으로 생성되었습니다.", model));
        } catch (Exception e) {
            log.error("공용 모델 생성 실패 - 이름: {}, 카테고리: {}, 에러: {}", name, categoryId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.of("공용 모델 생성 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 개인 모델 생성 (로그인 사용자)
     */
    @PostMapping("/personal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<ProductModelResponse>> createPersonalModel(
            @RequestParam("name") String name,
            @RequestParam("manualFile") MultipartFile manualFile,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            if (manualFile == null || manualFile.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(CommonResponse.of("매뉴얼 파일은 필수입니다.", null));
            }

            String filename = manualFile.getOriginalFilename();
            String contentType = manualFile.getContentType();
            
            if (filename == null || (!filename.toLowerCase().endsWith(".pdf")) || 
                (contentType != null && !contentType.toLowerCase().contains("pdf"))) {
                return ResponseEntity.badRequest()
                        .body(CommonResponse.of("PDF 파일만 업로드 가능합니다.", null));
            }

            User user = userDetails.getUser();
            log.info("개인 모델 생성 요청 - 이름: {}, 사용자: {}", name, user.getEmail());
            ProductModelResponse model = productModelService.createPersonalModel(name, manualFile, user.getEmail());
            log.info("개인 모델 생성 성공 - ID: {}", model.getId());

            return ResponseEntity.ok(CommonResponse.of("개인 모델이 성공적으로 생성되었습니다.", model));
        } catch (Exception e) {
            log.error("개인 모델 생성 실패 - 이름: {}, 사용자: {}, 에러: {}", name, userDetails.getUser().getEmail(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.of("개인 모델 생성 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 특정 카테고리의 공용 모델 조회
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductModelResponse>> getPublicModelsByCategory(@PathVariable Long categoryId) {
        log.info("카테고리별 공용 모델 조회 요청 - 카테고리 ID: {}", categoryId);
        try {
            List<ProductModelResponse> response = productModelService.getPublicModelsByCategory(categoryId).stream()
                    .map(ProductModelResponse::from)
                    .collect(Collectors.toList());
            log.info("카테고리 {} 공용 모델 조회 성공 - {} 개 모델 반환", categoryId, response.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("카테고리별 공용 모델 조회 실패 - 카테고리 ID: {}, 에러: {}", categoryId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 사용자의 개인 모델 목록 조회
     */
    @GetMapping("/personal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ProductModelResponse>> getUserModels(@AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        log.info("사용자 개인 모델 조회 요청 - 사용자 ID: {}", user.getId());
        List<ProductModelResponse> response = productModelService.getUserModels(user.getId()).stream()
                .map(ProductModelResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 공용 모델 수정 (관리자 전용)
     */
    @PutMapping("/public/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<ProductModelResponse>> updatePublicModel(
            @PathVariable Long id,
            @RequestBody ProductModelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("공용 모델 수정 요청 - 모델 ID: {}, 이름: {}, 카테고리: {}, 관리자: {}", 
                id, request.getName(), request.getCategoryId(), user.getId());
            ProductModel model = productModelService.updatePublicModel(id, request.getName(), request.getCategoryId());
            return ResponseEntity.ok(CommonResponse.of("공용 모델이 성공적으로 수정되었습니다.", ProductModelResponse.from(model)));
        } catch (Exception e) {
            log.error("공용 모델 수정 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.of("공용 모델 수정 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 개인 모델 수정 (소유자만 가능)
     */
    @PutMapping("/personal/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<ProductModelResponse>> updatePersonalModel(
            @PathVariable Long id,
            @RequestBody ProductModelRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("개인 모델 수정 요청 - 모델 ID: {}, 이름: {}", id, request.getName());
            ProductModel model = productModelService.updatePersonalModel(id, request.getName(), user.getId());
            return ResponseEntity.ok(CommonResponse.of("개인 모델이 성공적으로 수정되었습니다.", ProductModelResponse.from(model)));
        } catch (Exception e) {
            log.error("개인 모델 수정 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.of("개인 모델 수정 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 모든 모델 조회 (관리자 전용)
     */
    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductModelResponse>> getAllModels(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("전체 모델 조회 요청 - 관리자: {}", userDetails.getUser().getId());
        List<ProductModelResponse> response = productModelService.getAllModels().stream()
                .map(ProductModelResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /**
     * 개인 모델 삭제 (소유자만 가능)
     */
    @DeleteMapping("/personal/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CommonResponse<Void>> deletePersonalModel(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("개인 모델 삭제 요청 - 모델 ID: {}, 사용자 ID: {}", id, user.getId());
            productModelService.deletePersonalModel(id, user.getId());
            return ResponseEntity.ok(CommonResponse.of("개인 모델이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("개인 모델 삭제 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.of("개인 모델 삭제 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }

    /**
     * 모델 삭제 (관리자 전용)
     */
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<Void>> deleteModelByAdmin(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            User user = userDetails.getUser();
            log.info("관리자 모델 삭제 요청 - 모델 ID: {}, 관리자 ID: {}", id, user.getId());
            productModelService.deleteModelByAdmin(id);
            return ResponseEntity.ok(CommonResponse.of("모델이 성공적으로 삭제되었습니다.", null));
        } catch (Exception e) {
            log.error("관리자 모델 삭제 실패 - ID: {}, 에러: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.of("모델 삭제 중 오류가 발생했습니다: " + e.getMessage(), null));
        }
    }
}

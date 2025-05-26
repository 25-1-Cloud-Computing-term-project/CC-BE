package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.Manual;
import CC_BE.CC_BE.domain.ProductModel;
import CC_BE.CC_BE.service.ManualService;
import CC_BE.CC_BE.service.ProductModelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/manuals")
@RequiredArgsConstructor
public class ManualController {
    private final ManualService manualService;
    private final ProductModelService productModelService;

    /**
     * 특정 제품 모델의 매뉴얼을 다운로드합니다.
     */
    @GetMapping("/model/{modelId}/download")
    public ResponseEntity<Resource> downloadManual(@PathVariable Long modelId) {
        try {
            ProductModel model = productModelService.findById(modelId);
            if (model.getManual() == null) {
                return ResponseEntity.notFound().build();
            }
            
            Resource resource = manualService.loadManualAsResource(model.getManual());
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "attachment; filename=\"" + model.getManual().getFileName() + "\"")
                    .body(resource);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            log.error("매뉴얼 다운로드 실패 - 모델 ID: {}, 에러: {}", modelId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

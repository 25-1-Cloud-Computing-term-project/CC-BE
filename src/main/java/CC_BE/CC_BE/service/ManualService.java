package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.*;
import CC_BE.CC_BE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManualService {
    private final ManualRepository manualRepository;
    private final UserRepository userRepository;
    private final Path manualStorageLocation = Paths.get("uploads/manuals").toAbsolutePath();

    @Transactional
    public Manual uploadManual(MultipartFile file, ProductModel model, User uploader) throws IOException {
        // 디렉토리가 없으면 생성
        Files.createDirectories(manualStorageLocation);

        // 파일 이름 생성 (모델ID_timestamp.pdf)
        String fileName = String.format("%d_%d.pdf", model.getId(), System.currentTimeMillis());
        Path targetPath = manualStorageLocation.resolve(fileName);

        // 파일 저장
        Files.copy(file.getInputStream(), targetPath);

        // DB에 매뉴얼 정보 저장
        Manual manual = new Manual();
        manual.setFileName(file.getOriginalFilename());
        manual.setFilePath(fileName); // 파일명만 저장
        manual.setUploadDate(LocalDateTime.now());
        manual.setUploader(uploader);
        manual.setProductModel(model);

        return manualRepository.save(manual);
    }

    // 매뉴얼 다운로드
    public Optional<Manual> getManual(Long manualId) {
        return manualRepository.findById(manualId);
    }

    public Resource loadManualAsResource(Manual manual) throws IOException {
        Path filePath = manualStorageLocation.resolve(manual.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("매뉴얼 파일을 읽을 수 없습니다.");
        }
    }

    // 매뉴얼 삭제
    @Transactional
    public void deleteManual(Long modelId, User user) {
        Manual manual = manualRepository.findByProductModelId(modelId)
                .orElseThrow(() -> new IllegalArgumentException("매뉴얼을 찾을 수 없습니다."));
                
        if (user != null && !manual.getUploader().getId().equals(user.getId()) && !user.getRole().equals("ROLE_ADMIN")) {
            throw new SecurityException("매뉴얼을 삭제할 권한이 없습니다.");
        }
        
        if (manual.getFilePath() != null) {
            try {
                Path filePath = manualStorageLocation.resolve(manual.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("매뉴얼 파일 삭제 중 오류가 발생했습니다.", e);
            }
        }
        
        manualRepository.delete(manual);
    }

    // 내 매뉴얼 목록
    public List<Manual> getMyManuals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return manualRepository.findByUploader(user);
    }
}

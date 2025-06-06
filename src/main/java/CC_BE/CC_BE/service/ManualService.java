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
import java.util.UUID;

/**
 * 매뉴얼 파일 관리를 담당하는 서비스 클래스
 * 매뉴얼 파일의 업로드, 다운로드, 삭제 등의 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
public class ManualService {
    private final ManualRepository manualRepository;
    private final UserRepository userRepository;
    private final Path manualStorageLocation = Paths.get("uploads/manuals").toAbsolutePath();

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 매뉴얼 파일을 업로드하고 모델과 연결합니다.
     * 
     * @param file 업로드할 매뉴얼 파일
     * @param model 매뉴얼이 속할 제품 모델
     * @param uploader 파일을 업로드한 사용자
     * @return 저장된 매뉴얼 정보
     * @throws IOException 파일 저장 중 오류 발생 시
     */
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

    /**
     * ML 서버 연동을 위한 매뉴얼 파일 저장
     * 1. 파일을 로컬 스토리지에 저장합니다.
     * 2. 매뉴얼 정보를 데이터베이스에 저장합니다.
     * 3. ML 처리 상태를 false로 초기화합니다.
     *
     * @param file 저장할 매뉴얼 파일
     * @param modelName 매뉴얼이 속한 모델의 이름
     * @return 저장된 매뉴얼 정보
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    @Transactional
    public Manual saveManual(MultipartFile file, String modelName) throws IOException {
        // 디렉토리가 없으면 생성
        Files.createDirectories(manualStorageLocation);

        // 파일 이름 생성 (UUID + 원본 파일명)
        String originalFilename = file.getOriginalFilename();
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path targetPath = manualStorageLocation.resolve(uniqueFilename);

        // 파일 저장
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Manual 엔티티 생성 및 저장
        Manual manual = Manual.builder()
                .fileName(originalFilename)
                .filePath(uniqueFilename)  // 파일명만 저장
                .modelName(modelName)
                .uploadDate(LocalDateTime.now())
                .mlProcessed(false)
                .build();

        return manualRepository.save(manual);
    }

    /**
     * 매뉴얼 파일을 다운로드합니다.
     * 
     * @param manualId 다운로드할 매뉴얼의 ID
     * @return 매뉴얼 정보 (Optional)
     */
    public Optional<Manual> getManual(Long manualId) {
        return manualRepository.findById(manualId);
    }

    /**
     * 매뉴얼 파일을 Resource 형태로 로드합니다.
     * 
     * @param manual 로드할 매뉴얼 정보
     * @return 매뉴얼 파일 Resource
     * @throws IOException 파일 로드 중 오류 발생 시
     */
    public Resource loadManualAsResource(Manual manual) throws IOException {
        if (manual == null || manual.getFilePath() == null) {
            throw new IOException("매뉴얼 정보가 올바르지 않습니다.");
        }

        // 파일 경로 처리
        Path filePath;
        if (manual.getFilePath().contains("\\") || manual.getFilePath().contains("/")) {
            // 전체 경로가 저장된 경우 (이전 데이터)
            filePath = Paths.get(manual.getFilePath());
        } else {
            // 파일명만 저장된 경우 (새로운 방식)
            filePath = manualStorageLocation.resolve(manual.getFilePath());
        }

        Resource resource = new UrlResource(filePath.toUri());
        
        if (resource.exists() && resource.isReadable()) {
            return resource;
        } else {
            throw new IOException("매뉴얼 파일을 읽을 수 없습니다: " + filePath);
        }
    }

    /**
     * 매뉴얼을 삭제합니다.
     * 1. 파일 시스템에서 매뉴얼 파일을 삭제합니다.
     * 2. 데이터베이스에서 매뉴얼 정보를 삭제합니다.
     *
     * @param modelId 삭제할 매뉴얼이 속한 모델의 ID
     * @param user 삭제를 요청한 사용자
     * @throws SecurityException 삭제 권한이 없는 경우
     * @throws RuntimeException 파일 삭제 중 오류 발생 시
     */
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

    /**
     * 사용자가 업로드한 매뉴얼 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자가 업로드한 매뉴얼 목록
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    public List<Manual> getMyManuals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return manualRepository.findByUploader(user);
    }
}

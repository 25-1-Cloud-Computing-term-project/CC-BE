package CC_BE.CC_BE.config;

import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 서버 시작 시 기본 관리자 계정이 없다면 생성하는 초기화 컴포넌트
 */
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_EMAIL = "test1@gmail.com";
    private static final String ADMIN_PASSWORD = "1234";

    @Override
    @Transactional
    public void run(String... args) {
        // 이미 해당 이메일을 가진 관리자가 있는지 확인
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            // 관리자 계정 생성
            User adminUser = User.builder()
                    .email(ADMIN_EMAIL)
                    .password(passwordEncoder.encode(ADMIN_PASSWORD))
                    .role("ROLE_ADMIN")
                    .build();

            userRepository.save(adminUser);
            System.out.println("기본 관리자 계정이 생성되었습니다. (이메일: " + ADMIN_EMAIL + ")");
        }
    }
} 
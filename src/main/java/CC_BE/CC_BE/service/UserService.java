package CC_BE.CC_BE.service;

import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.repository.UserRepository;
import CC_BE.CC_BE.security.CustomUserDetails;
import CC_BE.CC_BE.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Map<String, String> signup(String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_USER"); // 기본 역할 설정
        userRepository.save(user);

        return Map.of("message", "회원가입이 완료되었습니다.");
    }

    @Transactional
    public Map<String, String> login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, password)
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        Map<String, String> response = new HashMap<>();
        response.put("message", "로그인이 완료되었습니다.");
        response.put("token", jwt);
        return response;
    }

    @Transactional
    public Map<String, String> logout() {
        SecurityContextHolder.clearContext();
        return Map.of("message", "로그아웃이 완료되었습니다.");
    }

    /**
     * 사용자 ID로 사용자를 조회합니다.
     * @param id 조회할 사용자의 ID
     * @return 조회된 사용자 정보
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + id));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + email));
    }
}
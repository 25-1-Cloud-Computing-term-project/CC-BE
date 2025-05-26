package CC_BE.CC_BE.controller;

import CC_BE.CC_BE.domain.User;
import CC_BE.CC_BE.dto.UserSignupRequest;
import CC_BE.CC_BE.dto.UserLoginRequest;
import CC_BE.CC_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    /**
     * 새로운 사용자를 등록합니다.
     * @param request 사용자 등록 요청 데이터
     * @return 등록 결과 메시지
     */
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@RequestBody UserSignupRequest request) {
        Map<String, String> response = userService.signup(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 로그인을 처리합니다.
     * @param request 로그인 요청 데이터
     * @return 로그인 결과 메시지와 JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody UserLoginRequest request) {
        Map<String, String> response = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자 로그아웃을 처리합니다.
     * @return 로그아웃 결과 메시지
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> response = userService.logout();
        return ResponseEntity.ok(response);
    }
}

package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 로그인 요청 DTO
 */
@Getter @Setter
public class UserLoginRequest {
    /**
     * 로그인할 사용자의 이메일
     */
    private String email;

    /**
     * 로그인할 사용자의 비밀번호
     */
    private String password;
} 
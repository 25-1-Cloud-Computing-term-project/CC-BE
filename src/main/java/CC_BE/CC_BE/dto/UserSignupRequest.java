package CC_BE.CC_BE.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 사용자 회원가입 요청 DTO
 */
@Getter @Setter
public class UserSignupRequest {
    /**
     * 가입할 사용자의 이메일
     */
    private String email;

    /**
     * 가입할 사용자의 비밀번호
     */
    private String password;
} 
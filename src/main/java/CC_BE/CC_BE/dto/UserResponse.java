package CC_BE.CC_BE.dto;

import CC_BE.CC_BE.domain.User;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserResponse {
    private Long id;
    private String email;

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        return response;
    }
} 
package CC_BE.CC_BE.security;

import CC_BE.CC_BE.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Add a getter method for the User object
    public User getUser() {
        return user;
    }

    /**
     * 사용자의 권한 목록을 반환합니다.
     * @return 사용자의 권한 목록
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole()));
    }

    /**
     * 사용자의 비밀번호를 반환합니다.
     * @return 사용자의 비밀번호
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자의 아이디를 반환합니다.
     * @return 사용자의 아이디
     */
    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * 계정이 만료되지 않았는지 확인합니다.
     * @return 계정 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 계정이 잠기지 않았는지 확인합니다.
     * @return 계정 잠금 여부 (true: 잠기지 않음)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 자격 증명이 만료되지 않았는지 확인합니다.
     * @return 자격 증명 만료 여부 (true: 만료되지 않음)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 계정이 활성화되어 있는지 확인합니다.
     * @return 계정 활성화 여부 (true: 활성화됨)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
package org.gamja.gamzatechblog.core.auth.jwt;

import lombok.Data;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole().name();
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + roleName)
        );
    }

    @Override
    public String getPassword() {
        throw new UnsupportedOperationException("소셜 로그인에서는 패스워드를 사용하지 않습니다.");
    }

    @Override
    public String getUsername() {
        return user.getGithubId();
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return true; }
}

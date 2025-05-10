package org.gamja.gamzatechblog.core.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByGithubId(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return new UserDetailsImpl(user);
    }
}

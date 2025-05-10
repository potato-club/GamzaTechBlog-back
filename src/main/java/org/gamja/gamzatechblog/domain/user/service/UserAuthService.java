package org.gamja.gamzatechblog.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.gamja.gamzatechblog.core.auth.oauth.model.OAuthUserInfo;
import org.gamja.gamzatechblog.domain.user.model.entity.User;
import org.gamja.gamzatechblog.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserAuthService {

    private final UserRepository userRepository;

    public boolean existsByProviderId(String providerId) {
        return userRepository.existsByGithubId(providerId);
    }

    @Transactional
    public User registerWithProvider(OAuthUserInfo info) {
        User user = User.builder()
                .githubId(info.getProviderId())
                .name(info.getName())
                .email(info.getEmail())
                .gamjaBatch(0)
                .build();
        return userRepository.save(user);
    }
}

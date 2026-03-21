package org.gamja.gamzatechblog.core.auth.oauth.handler;

import java.util.Map;

import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.dao.GithubOAuthTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.event.GithubPostLoginEvent;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubPostLoginHandler {

	private final GithubApiClient githubApiClient;
	private final GithubOAuthTokenDao githubTokenDao;
	private final UserService userService;

	@Async("postLoginExecutor")
	@EventListener
	public void handle(GithubPostLoginEvent postLoginEvent) {
		try {
			try {
				Map<String, Object> profile = githubApiClient.fetchProfile(postLoginEvent.accessToken());
				String profileImageUrl = (String)profile.get("avatar_url");
				userService.attachProfileImageIfAbsent(postLoginEvent.githubId(), profileImageUrl);
			} catch (Exception ex) {
				log.debug("깃허브 프로필 이미지 보정을 건너뜀 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
			}

			// 이메일 보정
			try {
				String email = githubApiClient.fetchPrimaryEmail(postLoginEvent.accessToken());
				if (email != null && !email.isBlank()) {
					userService.updateEmailIfEmpty(postLoginEvent.githubId(), email);
				}
			} catch (Exception ex) {
				log.debug("깃허브 기본 이메일 조회를 건너뜀 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
			}

			// 액세스 토큰 저장
			try {
				githubTokenDao.saveOrUpdateByGithubId(postLoginEvent.githubId(), postLoginEvent.accessToken());
			} catch (Exception ex) {
				log.warn("깃허브 토큰 저장 실패 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
			}
		} catch (Exception ex) {
			log.warn("로그인 후행 처리 실패 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
		}
	}
}

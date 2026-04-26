package org.gamja.gamzatechblog.core.auth.oauth.handler;

import java.util.Map;

import org.gamja.gamzatechblog.core.auth.oauth.client.GithubApiClient;
import org.gamja.gamzatechblog.core.auth.oauth.dao.GithubOAuthTokenDao;
import org.gamja.gamzatechblog.core.auth.oauth.event.GithubPostLoginEvent;
import org.gamja.gamzatechblog.domain.user.service.UserService;
import org.gamja.gamzatechblog.domain.user.service.model.GithubEnrichmentState;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GithubPostLoginHandler {

	private final GithubApiClient githubApiClient;
	private final GithubOAuthTokenDao githubTokenDao;
	private final UserService userService;
	private final OAuth2AuthorizedClientService authorizedClientService;

	@Async("postLoginExecutor")
	@EventListener
	public void handle(GithubPostLoginEvent postLoginEvent) {
		try {
			String githubAccessToken = resolveGithubAccessToken(postLoginEvent);
			if (!StringUtils.hasText(githubAccessToken)) {
				log.debug("깃허브 액세스 토큰이 없어 후행 보정을 건너뜀 githubId={}", postLoginEvent.githubId());
				return;
			}

			GithubEnrichmentState enrichmentState = userService.getGithubEnrichmentState(postLoginEvent.githubId());
			boolean needsProfileImage = enrichmentState.needsProfileImage();
			boolean needsEmail = enrichmentState.needsEmail();
			String profileImageUrl = null;
			String email = null;

			try {
				if (needsProfileImage) {
					Map<String, Object> profile = githubApiClient.fetchProfile(githubAccessToken);
					profileImageUrl = (String)profile.get("avatar_url");
				}
			} catch (Exception ex) {
				log.debug("깃허브 프로필 이미지 보정을 건너뜀 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
			}

			// 이메일 보정
			try {
				if (needsEmail) {
					email = githubApiClient.fetchPrimaryEmail(githubAccessToken);
				}
			} catch (Exception ex) {
				log.debug("깃허브 기본 이메일 조회를 건너뜀 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
			}

			if (StringUtils.hasText(profileImageUrl) || StringUtils.hasText(email)) {
				userService.enrichGithubDataIfMissing(postLoginEvent.githubId(), email, profileImageUrl);
			}

			// 액세스 토큰 저장
			try {
				githubTokenDao.saveOrUpdateByGithubId(postLoginEvent.githubId(), githubAccessToken);
			} catch (Exception ex) {
				log.warn("깃허브 토큰 저장 실패 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
			}
		} catch (Exception ex) {
			log.warn("로그인 후행 처리 실패 githubId={} 사유={}", postLoginEvent.githubId(), ex.toString());
		}
	}

	private String resolveGithubAccessToken(GithubPostLoginEvent postLoginEvent) {
		if (StringUtils.hasText(postLoginEvent.accessToken())) {
			return postLoginEvent.accessToken();
		}
		OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
			postLoginEvent.registrationId(),
			postLoginEvent.principalName()
		);
		if (client == null || client.getAccessToken() == null) {
			return null;
		}
		return client.getAccessToken().getTokenValue();
	}
}

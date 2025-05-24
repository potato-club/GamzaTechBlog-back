package org.gamja.gamzatechblog.core.auth.jwt.validator;

import org.gamja.gamzatechblog.core.auth.oauth.dao.GithubOAuthTokenDao;
import org.gamja.gamzatechblog.core.error.ErrorCode;
import org.gamja.gamzatechblog.core.error.exception.GithubOAuthTokenInvalidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class GithubTokenValidator {

	private final GithubOAuthTokenDao githubOAuthTokenDao;
	private final WebClient.Builder webClientBuilder;

	public String validateAndGetGitHubAccessToken(String githubUsername) {
		String githubAccessToken = githubOAuthTokenDao
			.findOauthAccessTokenByGithubId(githubUsername)
			.orElseThrow(() ->
				new GithubOAuthTokenInvalidException(ErrorCode.GITHUB_OAUTH_TOKEN_NOT_FOUND)
			);

		boolean tokenValid = webClientBuilder.build()
			.get()
			.uri("https://api.github.com/user")
			.header(HttpHeaders.AUTHORIZATION, "Bearer " + githubAccessToken)
			.retrieve()
			.toBodilessEntity()
			.map(resp -> resp.getStatusCode().is2xxSuccessful())
			.onErrorResume(ex -> {
				if (ex instanceof WebClientResponseException webEx
					&& webEx.getStatusCode() == HttpStatus.UNAUTHORIZED) {
					return Mono.just(false);
				}
				return Mono.error(ex);
			})
			.blockOptional()
			.orElse(false);

		if (!tokenValid) {
			throw new GithubOAuthTokenInvalidException(ErrorCode.GITHUB_OAUTH_TOKEN_INVALID);
		}

		return githubAccessToken;
	}
}